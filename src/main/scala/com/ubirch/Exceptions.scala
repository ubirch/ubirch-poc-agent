package com.ubirch

import scala.util.control.NoStackTrace

abstract class ServiceException(message: String) extends Exception(message) with NoStackTrace {
  val name: String = this.getClass.getCanonicalName
}

case class HttpResponseException(targetSystem: Symbol, statusCode: Int, headers: Map[String, String], body: String) extends ServiceException(body)

case class InternalException(message: String) extends Exception(message)
