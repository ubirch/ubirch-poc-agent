package com.ubirch.services.external

import com.typesafe.config.Config
import com.ubirch.models.requests.{ CertificationRequest, UPPSigningRequest }
import com.ubirch.models.responses.SigningResponse
import com.ubirch.services.execution.SttpBackendProvider
import com.ubirch.{ ConfPaths, InternalException }
import monix.eval.Task
import org.json4s.Formats
import org.json4s.jackson.JsonMethods.{ compact, parse }
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import sttp.client3._
import sttp.client3.json4s.asJsonAlways
import sttp.model.MediaType

import javax.inject.Inject

trait GoClientService {
  def sign(certificationRequest: CertificationRequest): Task[SigningResponse]
}

class GoClientServiceImpl @Inject() (sttpBackendProvider: SttpBackendProvider, conf: Config)(implicit formats: Formats)
  extends GoClientService {

  private val deviceId = conf.getString(ConfPaths.UppSigningPaths.DEVICE_ID)
  private val devicePwd = conf.getString(ConfPaths.UppSigningPaths.DEVICE_PWD)
  private val endpoint = conf.getString(ConfPaths.UppSigningPaths.ENDPOINT)

  implicit private val serialization: Serialization.type = org.json4s.native.Serialization

  override def sign(certificationRequest: CertificationRequest): Task[SigningResponse] = {
    val uppSigningRequests = UPPSigningRequest.fromCertificationRequest(certificationRequest)

    def buildRequest(request: UPPSigningRequest) = basicRequest
      .header("X-Auth-Token", devicePwd)
      .contentType(MediaType.ApplicationJson)
      .body(compact(parse(write(request))))
      .post(uri"$endpoint/${deviceId}")
      .response(asJsonAlways[SigningResponse])

    def sendRequest(request: UPPSigningRequest) = {
      Task.fromFuture(
        sttpBackendProvider
          .backend
          .send(buildRequest(request)))
        .flatMap(_.body match {
          case Right(response) => Task(response)
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
          InternalException(s"Expected to have only one test in the request, but instead got ${otherwise.size}"))
    }
  }

}
