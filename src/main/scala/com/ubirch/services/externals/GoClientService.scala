package com.ubirch.services.externals

import java.util.UUID

import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config
import com.ubirch.models.requests.{ CertificationRequest, UPPSigningRequest }
import com.ubirch.models.responses.SigningResponse
import com.ubirch.services.execution.HttpClientProvider
import com.ubirch.{ ConfPaths, HttpResponseException, InternalException }
import monix.eval.Task
import monix.execution.Scheduler
import org.json4s.jackson.JsonMethods.{ compact, parse }
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import org.json4s.{ Formats, JValue }
import sttp.client3._
import sttp.client3.json4s._
import sttp.model.MediaType

import scala.util.Try

trait GoClientService {
  def sign(certificationRequest: CertificationRequest, deviceId: UUID, devicePwd: String): Task[SigningResponse]
}

class GoClientServiceImpl @Inject() (
    conf: Config,
    httpClient: HttpClientProvider,
    @Named("io") scheduler: Scheduler
)(implicit formats: Formats)
  extends GoClientService {

  private val endpoint = conf.getString(ConfPaths.UppSigningPaths.ENDPOINT)

  implicit private val serialization: Serialization.type = org.json4s.native.Serialization

  override def sign(
      certificationRequest: CertificationRequest,
      deviceId: UUID,
      devicePwd: String
  ): Task[SigningResponse] = {
    val uppSigningRequests = UPPSigningRequest.fromCertificationRequest(certificationRequest)

    def buildRequest(request: UPPSigningRequest) = basicRequest
      .header("X-Auth-Token", devicePwd)
      .contentType(MediaType.ApplicationJson)
      .body(compact(parse(write(request))))
      .post(uri"$endpoint/${deviceId.toString}")
      .response(asJson[SigningResponse])

    def sendRequest(request: UPPSigningRequest) = {
      Task.fromFuture(
        httpClient
          .backend
          .send(buildRequest(request))
      ).executeOn(scheduler)
        .flatMap(r =>
          r.body match {
            case Right(response) => Task(response)
            case Left(error: HttpError[String]) =>

              val tryBody = Try(Serialization.read[JValue](error.body))
              val headers = tryBody.map { parsedBody =>
                val maybeErrorId = (parsedBody \ "response" \ "header" \ "X-Err").extractOpt[List[String]].getOrElse(Nil).headOption.map(x => Map("X-Err" -> x)).getOrElse(Map.empty)
                val maybeRequestId = (parsedBody \ "requestID").extractOpt[String].map(x => Map("requestID" -> x)).getOrElse(Map.empty)
                maybeErrorId ++ maybeRequestId
              }.getOrElse(Map.empty)

              Task.raiseError(HttpResponseException(
                Symbol("UPP Signer"),
                "Error processing UPP Signer request",
                r.code.code,
                headers,
                if (tryBody.isSuccess) "Error anchoring upp: " + headers.mkString(", ") else error.body.trim
              ))

            case Left(error) =>
              Task.raiseError(InternalException(s"Failed to send UPP signing request because: ${error.getMessage}"))
          })
    }

    for {
      request <- retrieveOnlyOneUppTestRequest(uppSigningRequests)
      response <- sendRequest(request)
    } yield response
  }

  private def retrieveOnlyOneUppTestRequest(requests: List[UPPSigningRequest]) = {
    requests match {
      case request :: Nil => Task(request)
      case otherwise => Task.raiseError(
        InternalException(s"Expected to have only one test in the request, but instead got ${otherwise.size}")
      )
    }
  }

}
