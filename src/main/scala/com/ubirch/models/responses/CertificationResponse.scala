package com.ubirch.models.responses

case class CertificationResponse(
    hash: String,
    upp: String,
    dcc: Option[String],
    pdf: Option[String],
    response: Response,
    requestId: String,
    error: Option[String]
)
