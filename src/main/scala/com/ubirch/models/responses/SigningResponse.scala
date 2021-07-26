package com.ubirch.models.responses

//Response:: https://github.com/ubirch/ubirch-client-go#upp-signing-response

case class SigningResponse(
    hash: String, //Base64 encoded
    upp: String, // Base64 encoded
    response: Response,
    requestID: Option[String],
    error: Option[String]
)
