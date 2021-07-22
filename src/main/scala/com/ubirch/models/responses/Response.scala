package com.ubirch.models.responses

case class Response(statusCode: Int, header: Map[String, List[String]], content: String)
