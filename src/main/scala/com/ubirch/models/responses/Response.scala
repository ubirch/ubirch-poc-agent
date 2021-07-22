package com.ubirch.models.responses

case class Response(statusCode: Int, header: Map[String, String], content: String)
