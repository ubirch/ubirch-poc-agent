package com.ubirch.services.external

import com.typesafe.config.Config
import com.ubirch.models.requests.CertificationRequest
import com.ubirch.models.responses.CertifyApiResponse
import com.ubirch.services.execution.SttpSSLBackendProvider
import com.ubirch.{ ConfPaths, InternalException }
import monix.eval.Task
import org.json4s.Formats
import org.json4s.jackson.JsonMethods.{ compact, parse }
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import sttp.client3._
import sttp.client3.json4s.asJson
import sttp.model.HeaderNames.Accept
import sttp.model.MediaType

import javax.inject.Inject

trait CertifyApiService {
  def registerSeal(certificationRequest: CertificationRequest, contentType: MediaType): Task[CertifyApiResponse]
}

class CertifyApiServiceImpl @Inject() (sttpSSLBackendProvider: SttpSSLBackendProvider, conf: Config)(implicit
formats: Formats)
  extends CertifyApiService {

  private val endpoint = conf.getString(ConfPaths.CertifyPaths.ENDPOINT)

  implicit private val serialization: Serialization.type = org.json4s.native.Serialization

  override def registerSeal(
    certificationRequest: CertificationRequest,
    contentType: MediaType): Task[CertifyApiResponse] = {

    val request = basicRequest
      .contentType(MediaType.ApplicationJson)
      .header(Accept, contentType.toString)
      .body(compact(parse(write(certificationRequest))))
      .post(uri"$endpoint/api/certify/v2/issue")
      .response(asJson[CertifyApiResponse])

    def sendRequest() = {
      Task.fromFuture(
        sttpSSLBackendProvider
          .backend
          .send(request)
      ).flatMap(_.body match {
        case Right(response) => Task(response)
        case Left(error) =>
          Task.raiseError(InternalException(s"Failed to send Certify API request due to: ${error.getMessage}"))
      })
    }

    sendRequest()
  }
}
