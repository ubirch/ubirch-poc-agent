package com.ubirch

import com.ubirch.controllers.concerns.HeaderKeys
import monix.eval.Task
import sttp.model.MediaType

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

  private val allowedMediaTypes = List(MediaType.ApplicationJson)

  def getContentType(request: HttpServletRequest): Task[Option[MediaType]] = {
    for {
      ct <- getHeader(request, HeaderKeys.CONTENT_TYPE)
      mediaType <- Task.fromEither(error => InternalException(error))(MediaType.parse(ct))
    } yield {
      allowedMediaTypes.find(_ == mediaType)
    }
  }

}
