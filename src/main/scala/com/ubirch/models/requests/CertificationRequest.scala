package com.ubirch.models.requests

case class CertificationRequest(
  nam: Name,
  dob: String,
  t: List[Test]
)

case class Name(fn: String, gn: String)

case class Test(
  id: String,
  se: String,
  tg: String,
  tt: String,
  nm: String,
  tr: String,
  sc: String,
  tc: String
)
