package com.ubirch.models

import com.ubirch.Service

/**
  * Represents a simple Response object. Used for HTTP responses.
  */
abstract class Response[T] {
  val version: String
  val ok: T
}

object Response {
  final val version = Service.version
}

case class Return(version: String, ok: Boolean, data: Any) extends Response[Boolean]

object Return {
  def ok(data: Any): Return = new Return(Response.version, ok = true, data)
  def nok(data: String): Return = new Return(Response.version, ok = false, data)
}

