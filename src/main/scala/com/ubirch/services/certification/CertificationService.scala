package com.ubirch.services.certification

import com.ubirch.models.requests.CertificationRequest
import com.ubirch.models.responses.CertificationResponse
import com.ubirch.services.externals.{ CertifyApiService, GoClientService }
import monix.eval.Task
import nl.minvws.encoding.Base45
import sttp.model.MediaType

import java.util.Base64
import javax.inject.Inject

trait CertificationService {
  def performCertification(
      certificationRequest: CertificationRequest,
      mediaType: MediaType
  ): Task[CertificationResponse]
}

class CertificationServiceImpl @Inject() (goClientService: GoClientService, certifyApiService: CertifyApiService)
  extends CertificationService {
  override def performCertification(
      certificationRequest: CertificationRequest,
      mediaType: MediaType
  ): Task[CertificationResponse] = {
    for {
      certifyResponse <- certifyApiService.registerSeal(certificationRequest, mediaType)
      signingResponse <- goClientService.sign(certificationRequest)
    } yield CertificationResponse(
      signingResponse.hash,
      signingResponse.upp,
      if (isMediaTypePdf(mediaType)) Some(Base45.getEncoder.encodeToString(certifyResponse.body)) else None,
      if (!isMediaTypePdf(mediaType)) Some(Base64.getEncoder.encodeToString(certifyResponse.body)) else None,
      signingResponse.response,
      signingResponse.requestId,
      signingResponse.error
    )
  }

  private def isMediaTypePdf(mediaType: MediaType) = if (mediaType == MediaType.ApplicationPdf) true else false
}
