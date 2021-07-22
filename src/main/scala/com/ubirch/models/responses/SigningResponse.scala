package com.ubirch.models.responses

case class SigningResponse(
  hash: String, //Base64 encoded
  upp: String, // Base64 encoded
  response: Response,
  requestId: String,
  error: Option[String]
)