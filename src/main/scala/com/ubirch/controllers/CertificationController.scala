package com.ubirch.controllers

import com.typesafe.config.Config
import com.ubirch.ConfPaths.GenericConfPaths
import com.ubirch.controllers.concerns.{ ControllerBase, HeaderKeys }
import com.ubirch.models.NOK
import com.ubirch.models.requests.CertificationRequest
import com.ubirch.models.responses.{ CertificationResponse, Response }
import com.ubirch.util.TaskHelpers
import io.prometheus.client.Counter
import monix.eval.Task
import monix.execution.Scheduler
import org.json4s.Formats
import org.scalatra.swagger.{ ResponseMessage, Swagger, SwaggerSupportSyntax }
import org.scalatra.{ NotFound, Ok }

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CertificationController @Inject() (
  config: Config,
  val swagger: Swagger,
  jFormats: Formats
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

  post("/certification", operation(certification)) {
    asyncResult("certification") { implicit request => _ =>
      for {
        contentType <- getContentType(request)
        _ <- earlyResponseIf(contentType.isEmpty)(new IllegalArgumentException(s"No ${HeaderKeys.CONTENT_TYPE} found"))
        certificationRequest <- Task(ReadBody.readJson[CertificationRequest](x => x).extracted)
        _ = println(certificationRequest)
      } yield Ok(CertificationResponse(
        "hash",
        "upp",
        Some("dcc"),
        None,
        Response(200, Map.empty[String, String], "content"),
        "requestId",
        Some("error")))
    }
  }

  before() {
    contentType = "application/cbor"
  }

  notFound {
    asyncResult("not_found") { implicit request => _ =>
      Task {
        logger.info(
          "controller=CertificationController route_not_found={} query_string={}",
          requestPath,
          Option(request).map(_.getQueryString).getOrElse(""))
        NotFound(NOK.noRouteFound(requestPath + " might exist in another universe"))
      }
    }
  }
}
