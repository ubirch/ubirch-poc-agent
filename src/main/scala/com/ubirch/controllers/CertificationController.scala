package com.ubirch.controllers

import java.util.UUID

import com.typesafe.config.Config
import com.ubirch.ConfPaths.GenericConfPaths
import com.ubirch.HttpResponseException
import com.ubirch.controllers.concerns.{ ControllerBase, HeaderKeys }
import com.ubirch.models.{ Accepts, NOK }
import com.ubirch.models.requests.CertificationRequest
import com.ubirch.models.responses.{ CertificationResponse, SigningResponse }
import com.ubirch.services.certification.CertificationService
import com.ubirch.util.TaskHelpers
import io.prometheus.client.Counter
import monix.eval.Task
import monix.execution.Scheduler
import org.json4s.Formats
import org.scalatra._
import org.scalatra.swagger.{ ResponseMessage, Swagger, SwaggerSupportSyntax }
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.util.Try

class CertificationController @Inject() (
    config: Config,
    val swagger: Swagger,
    jFormats: Formats,
    certificationService: CertificationService
)(implicit val executor: ExecutionContext, scheduler: Scheduler)
  extends ControllerBase
  with TaskHelpers {

  override protected val applicationDescription = "PoC Agent Controller"
  implicit override protected def jsonFormats: Formats = jFormats

  val service: String = config.getString(GenericConfPaths.NAME)

  override val successCounter: Counter = Counter.build()
    .name("poc_agent_events_success")
    .help("Represents the number of certification events successes")
    .labelNames("service", "method")
    .register()

  override val errorCounter: Counter = Counter.build()
    .name("poc_agent_events_failures")
    .help("Represents the number of certification events failures")
    .labelNames("service", "method")
    .register()

  val certification: SwaggerSupportSyntax.OperationBuilder =
    (apiOperation[CertificationResponse]("certification")
      consumes "application/json"
      produces "application/json"
      summary "Certification"
      description "Certification"
      tags "Certification"
      parameters bodyParam[CertificationRequest]("certification request")
      responseMessages (
        ResponseMessage(
          400,
          "Invalid Request"
        ),
          ResponseMessage(
            500,
            "Internal Server Error"
          )
      ))

  get("/") {
    redirect("/info")
  }

  post("/:deviceId", operation(certification)) {
    asyncResult("certification") { implicit request => _ =>
      (for {
        _ <- Task.unit // this is here to make it part of the flatmap
        deviceId <- Task.fromTry(Try(UUID.fromString(params("deviceId")(request))))
          .onErrorRecoverWith {
            case _: Exception => Task.raiseError(new IllegalArgumentException("Expected deviceId in UUID format"))
          }
        devicePwd <- getHeader(request, HeaderKeys.AUTH_TOKEN)
        maybeAcceptHeader <- getAccept(request)
        _ = if (maybeAcceptHeader.isEmpty) logger.debug("No accept header found, assuming application/cbor")
        certificationRequest <- Task(ReadBody.readJson[CertificationRequest](x => x).extracted)
        response <- certificationService.performCertification(
          certificationRequest,
          maybeAcceptHeader.getOrElse(Accepts.ContentTypeCbor),
          deviceId,
          devicePwd
        )
      } yield Ok(response)).onErrorRecover {
        case e: HttpResponseException[_] =>
          logger.error(s"HttpResponseException ::  http_code=${e.statusCode} error=${e.message}")
          //TODO: Unify responses
          e.body match {
            case body: String => ActionResult(e.statusCode, NOK.pocAgentError(body), Map.empty)
            case body: SigningResponse => ActionResult(e.statusCode, body, Map.empty)
          }
        case e: IllegalArgumentException =>
          logger.error(
            s"IllegalArgumentException :: exception=${e.getClass.getCanonicalName} message=${e.getMessage}",
            e
          )
          BadRequest(NOK.pocAgentError(s"Sorry, there is something invalid in your request: ${e.getMessage}"))
        case e: Exception =>
          logger.error(s"Exception :: exception=${e.getClass.getCanonicalName} message=${e.getMessage} -> ", e)
          InternalServerError(NOK.pocAgentError("Sorry, something went wrong on our end"))
      }
    }
  }

  before() {
    contentType = "application/json"
  }

  notFound {
    asyncResult("not_found") { implicit request => _ =>
      Task {
        logger.info(
          "controller=CertificationController route_not_found={} query_string={}",
          requestPath,
          Option(request).map(_.getQueryString).getOrElse("")
        )
        NotFound(NOK.noRouteFound(requestPath + " might exist in another universe"))
      }
    }
  }

}
