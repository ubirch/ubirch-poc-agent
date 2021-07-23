package com.ubirch.models

import sttp.model.MediaType

object Accepts {
  final val ContentTypeCbor = MediaType("application", "cbor", Some("utf-8"))
  final val ContentTypeCborBase45 = MediaType("application", "cbor+base45")
  final val ContentTypePDF = MediaType.ApplicationPdf

  def isMediaTypePdf(mediaType: MediaType): Boolean = if (mediaType == ContentTypePDF) true else false
  def isMediaTypeCbor(mediaType: MediaType): Boolean = if (mediaType == ContentTypeCbor) true else false
  def isMediaTypeCborBase45(mediaType: MediaType): Boolean = if (mediaType == ContentTypeCborBase45) true else false

}
