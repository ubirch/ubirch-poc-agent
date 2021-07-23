package com.ubirch

import scala.util.control.NoStackTrace

abstract class ServiceException(message: String) extends Exception(message) with NoStackTrace {
  val name: String = this.getClass.getCanonicalName
}

case class HttpResponseException[T](targetSystem: Symbol, message: String, statusCode: Int, headers: Map[String, String], body: T) extends ServiceException(message)

case class InternalException(message: String) extends Exception(message)
