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

  //Note that ma is not mapped into the upp.
  //In case we needed we should include it to the case class
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
