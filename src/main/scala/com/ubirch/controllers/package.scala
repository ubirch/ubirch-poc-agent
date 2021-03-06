package com.ubirch

import com.ubirch.models.Accepts.{ ContentTypeCbor, ContentTypeCborBase45, ContentTypePDF }
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
    Try {
      request.getHeader(key)
    }
      .filter(x => Option(x).isDefined)
      .filter(_.nonEmpty)
      .recover {
        case _: Exception => throw new IllegalArgumentException(s"Header is missing ($key)")
      }
  }

  private val allowedAccept = List(ContentTypeCbor, ContentTypePDF, ContentTypeCborBase45)

  def getAccept(request: HttpServletRequest): Task[Option[MediaType]] = {
    // pick up first accept matching allowed accept headers
    Task.delay(request.getHeader(HeaderKeys.ACCEPT))
      .map(Option.apply)
      .map {
        case Some(accept) =>
          accept.split(",")
            .map(_.trim)
            .headOption
            .flatMap(x => MediaType.parse(x).toOption)
            .filter(x => allowedAccept.contains(x))
        case None => None
      }

  }

}
