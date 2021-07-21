package com.ubirch

import com.ubirch.controllers.concerns.HeaderKeys
import monix.eval.Task

import javax.servlet.http.HttpServletRequest
import scala.util.Try

package object controllers {

  /**
    * get header by a key
    * When the header is not found, an error is threw
    */
  def getHeader(request: HttpServletRequest, key: String): Task[String] = Task.fromTry {
    Try { request.getHeader(key) }
      .filter(x => Option(x).isDefined)
      .filter(_.nonEmpty)
      .recover {
        case _: Exception => throw new IllegalArgumentException(s"Header is missing ($key)")
      }
  }

  private val allowedCT = List(HeaderKeys.ContentTypeJSON).map(_.toLowerCase)

  def getContentType(request: HttpServletRequest): Task[Option[String]] = {
    for {
      //Should fail if nothing provided
      ct <- getHeader(request, HeaderKeys.CONTENT_TYPE)
    } yield {
      ct.split(";")
        .map(_.trim)
        .headOption
        .filter(x => allowedCT.contains(x.toLowerCase))
    }
  }

}
