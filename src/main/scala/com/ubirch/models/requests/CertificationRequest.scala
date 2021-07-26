package com.ubirch.models.requests

case class CertificationRequest(
    nam: Name,
    dob: String,
    t: List[Test]
)

case class Name(fn: String, gn: String)

//For more reference check:
//https://github.com/ubirch/ubirch-certify-service/blob/main/src/main/scala/com/ubirch/models/DigitalGreenCertificateSeed.scala#L10
case class Test(
    id: String,
    tg: String,
    tt: String,
    nm: Option[String],
    ma: Option[String],
    tr: String,
    sc: String,
    tc: String,
    se: String // This value is not part of the dcc standard
)
