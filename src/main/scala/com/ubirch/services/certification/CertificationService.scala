package com.ubirch.services.certification

import java.util.{ Base64, UUID }

import com.ubirch.models.requests.CertificationRequest
import com.ubirch.models.responses.CertificationResponse
import com.ubirch.services.externals.{ CertifyApiService, GoClientService }
import monix.eval.Task
import nl.minvws.encoding.Base45
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
  extends CertificationService {
  override def performCertification(
      certificationRequest: CertificationRequest,
      mediaType: MediaType,
      deviceId: UUID,
      devicePwd: String
  ): Task[CertificationResponse] = {
    for {
      certifyResponse <- certifyApiService.registerSeal(certificationRequest, mediaType)
      signingResponse <- goClientService.sign(certificationRequest, deviceId, devicePwd)
    } yield CertificationResponse(
      hash = signingResponse.hash,
      upp = signingResponse.upp,
      dcc = if (!isMediaTypePdf(mediaType)) Some(Base45.getEncoder.encodeToString(certifyResponse.body)) else None,
      pdf = if (isMediaTypePdf(mediaType)) Some(Base64.getEncoder.encodeToString(certifyResponse.body)) else None,
      response = signingResponse.response,
      requestId = signingResponse.requestId,
      error = signingResponse.error
    )
  }

  private def isMediaTypePdf(mediaType: MediaType) = if (mediaType == MediaType.ApplicationPdf) true else false
}
