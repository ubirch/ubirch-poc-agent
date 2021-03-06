package com.ubirch.services.certification

import java.util.{ Base64, UUID }

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.models.Accepts
import com.ubirch.models.requests.CertificationRequest
import com.ubirch.models.responses.CertificationResponse
import com.ubirch.services.externals.{ CertifyApiService, GoClientService }
import monix.eval.Task
import sttp.model.MediaType
import javax.inject.Inject

trait CertificationService {
  def performCertification(
      certificationRequest: CertificationRequest,
      mediaType: MediaType,
      deviceId: UUID,
      devicePwd: String
  ): Task[CertificationResponse]
}

class CertificationServiceImpl @Inject() (goClientService: GoClientService, certifyApiService: CertifyApiService)
  extends CertificationService with LazyLogging {

  override def performCertification(
      certificationRequest: CertificationRequest,
      mediaType: MediaType,
      deviceId: UUID,
      devicePwd: String
  ): Task[CertificationResponse] = {
    for {
      certifyResponse <- certifyApiService.registerSeal(certificationRequest, mediaType)
      signingResponse <- goClientService.sign(certificationRequest, deviceId, devicePwd)
    } yield {

      val dcc = if (Accepts.isMediaTypeCbor(mediaType)) {
        Option(Base64.getEncoder.encodeToString(certifyResponse.body))
      } else if (Accepts.isMediaTypeCborBase45(mediaType)) {
        Option(new String(certifyResponse.body))
      } else None

      val res = CertificationResponse(
        hash = signingResponse.hash,
        upp = signingResponse.upp,
        dcc = dcc,
        pdf = if (Accepts.isMediaTypePdf(mediaType)) Some(Base64.getEncoder.encodeToString(certifyResponse.body)) else None,
        response = signingResponse.response,
        requestID = signingResponse.requestID,
        dccID = certifyResponse.`X-DGC-ID`,
        error = signingResponse.error
      )

      logger.info(s"upp_dcc_certification=${res.hash} request_id=${res.requestID.getOrElse("-")} X-DGC-ID=${certifyResponse.`X-DGC-ID`.getOrElse("-")}")

      res

    }
  }

}
