package com.ubirch.controllers.concerns

object CorsConfig {

  final val DefaultAllowedHeaders: Seq[String] = Seq(
    "Cookie",
    "Host",
    "X-Forwarded-For",
    "Accept-Charset",
    "If-Modified-Since",
    "Accept-Language",
    "X-Forwarded-Port",
    "Connection",
    "X-Forwarded-Proto",
    "User-Agent",
    "Referer",
    "Accept-Encoding",
    "X-Requested-With",
    "Authorization",
    "Accept",
    "Content-Type"
  )

  final val DefaultMethods: String = "GET,POST,OPTIONS"

}
