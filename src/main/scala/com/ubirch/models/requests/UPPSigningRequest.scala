package com.ubirch.models.requests

case class UPPSigningRequest(
    f: String,
    g: String,
    b: String,
    p: String,
    i: String,
    d: String,
    t: String,
    r: String,
    s: String
)

object UPPSigningRequest {
  def fromCertificationRequest(certificationRequest: CertificationRequest): List[UPPSigningRequest] =
    certificationRequest.t.map(test =>
      UPPSigningRequest(
        certificationRequest.nam.fn,
        certificationRequest.nam.gn,
        certificationRequest.dob,
        test.tc,
        test.id,
        test.sc,
        test.tt,
        test.tr,
        test.se
      ))
}
