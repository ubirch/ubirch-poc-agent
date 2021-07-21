package com.ubirch.controllers

import com.typesafe.config.Config
import com.ubirch.ConfPaths.GenericConfPaths
import com.ubirch.controllers.concerns.ControllerBase
import com.ubirch.models.{ NOK, Return }
import io.prometheus.client.Counter
import monix.eval.Task
import monix.execution.Scheduler
import org.json4s.Formats
import org.scalatra._
import org.scalatra.swagger.Swagger

import javax.inject._
import scala.concurrent.ExecutionContext

/**
  * Represents a simple controller for the base path "/"
  * @param swagger Represents the Swagger Engine.
  * @param jFormats Represents the json formats for the system.
  */

@Singleton
class InfoController @Inject() (config: Config, val swagger: Swagger, jFormats: Formats)(implicit val executor: ExecutionContext, scheduler: Scheduler)
  extends ControllerBase {

  override protected val applicationDescription = "Info Controller"
  override protected implicit def jsonFormats: Formats = jFormats

  val service: String = config.getString(GenericConfPaths.NAME)

  val successCounter: Counter = Counter.build()
    .name("info_management_success")
    .help("Represents the number of info management successes")
    .labelNames("service", "method")
    .register()

  val errorCounter: Counter = Counter.build()
    .name("info_management_failures")
    .help("Represents the number of info management failures")
    .labelNames("service", "method")
    .register()

  get("/") {
    asyncResult("root") { _ => _ =>
      Task {
        Ok(Return("Hallo, Hola, こんにちは, Hello, Salut, Hej, this is the Ubirch Point of Certification agent."))
      }
    }
  }

  before() {
    contentType = formats("json")
  }

  notFound {
    asyncResult("not_found") { implicit request => _ =>
      Task {
        logger.info("controller=InfoController route_not_found={} query_string={}", requestPath, Option(request).map(_.getQueryString).getOrElse(""))
        NotFound(NOK.noRouteFound(requestPath + " might exist in another universe"))
      }
    }
  }

}
