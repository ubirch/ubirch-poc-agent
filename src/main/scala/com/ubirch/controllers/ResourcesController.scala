package com.ubirch.controllers

import com.ubirch.controllers.concerns.RequestEnricher
import org.scalatra.ScalatraServlet
import org.scalatra.swagger._

import javax.inject._

/**
  *  Represents the Resource Controller that allows to serve public files: The Swagger UI.
  * @param swagger Represents the Swagger Engine.
  */
@Singleton
class ResourcesController @Inject() (val swagger: Swagger) extends ScalatraServlet with RequestEnricher with NativeSwaggerBase

object RestApiInfo extends ApiInfo(
  "PoC Agent Service",
  "These are the available endpoints for the PoC Agent Service. For more information drop me an email at carlos.sanchez at ubirch.com",
  "https://ubirch.de",
  ContactInfo("carlos sanchez", "https://ubirch.de", "carlos.sanchez@ubirch.com"),
  LicenseInfo("Apache License, Version 2.0", "https://www.apache.org/licenses/LICENSE-2.0")

)
