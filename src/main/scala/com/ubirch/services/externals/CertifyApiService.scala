package com.ubirch.services.externals

import com.google.inject.name.Named
import com.typesafe.config.Config
import com.ubirch.models.requests.CertificationRequest
import com.ubirch.models.responses.CertifyApiResponse
import com.ubirch.services.execution.SttpSSLBackendProvider
import com.ubirch.{ ConfPaths, HttpResponseException }
import monix.eval.Task
import monix.execution.Scheduler
import org.json4s.Formats
import org.json4s.jackson.JsonMethods.{ compact, parse }
import org.json4s.native.Serialization.write
import sttp.client3._
import sttp.model.HeaderNames.Accept
import sttp.model.MediaType

import javax.inject.Inject

trait CertifyApiService {
  def registerSeal(certificationRequest: CertificationRequest, contentType: MediaType): Task[CertifyApiResponse]
}

class CertifyApiServiceImpl @Inject() (
    sttpSSLBackendProvider: SttpSSLBackendProvider,
    conf: Config,
    @Named("io") scheduler: Scheduler
)(implicit formats: Formats)
  extends CertifyApiService {

  private val endpoint = conf.getString(ConfPaths.CertifyPaths.ENDPOINT)

  override def registerSeal(
      certificationRequest: CertificationRequest,
      contentType: MediaType
  ): Task[CertifyApiResponse] = {

    val request = basicRequest
      .contentType(MediaType("application", "vnd.dgc.v1.3+json"))
      .header(Accept, contentType.toString)
      .body(compact(parse(write(certificationRequest))))
      .post(uri"$endpoint/api/certify/v2/issue")
      .response(asByteArray)

    def sendRequest(): Task[CertifyApiResponse] = {
      Task.fromFuture(
        sttpSSLBackendProvider
          .backend
          .send(request)
      ).executeOn(scheduler).flatMap(r =>
          r.body match {
            case Right(response) =>
              Task(CertifyApiResponse(response))
            case Left(error) =>
              Task.raiseError(HttpResponseException(
                Symbol("Certify API"),
                "Error processing Certify API request",
                r.code.code,
                r.headers.map(x => (x.name, x.value)).toMap,
                error
              ))
          })
    }

    sendRequest()
  }
}
