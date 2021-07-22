import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.ConfPaths.HttpServerConfPaths
import com.ubirch.Service
import com.ubirch.controllers.{ CertificationController, InfoController, ResourcesController }
import com.ubirch.controllers.concerns.CorsConfig
import org.scalatra.LifeCycle

import javax.servlet.ServletContext

/**
  * Represents the configuration of controllers
  */
class ScalatraBootstrap extends LifeCycle with LazyLogging {

  lazy val config: Config = Service.get[Config]
  lazy val allowedCorsHeaders: String = (CorsConfig.DefaultAllowedHeaders ++ config.getString(
    HttpServerConfPaths.GENERAL_ADDITIONAL_CORS_HEADERS
  ).split(",").toSeq.filter(_.nonEmpty)).mkString(",")
  lazy val swaggerOnPath: Boolean = config.getBoolean(HttpServerConfPaths.SWAGGER_ACTIVATED_PATH)

  lazy val infoController: InfoController = Service.get[InfoController]
  lazy val resourceController: ResourcesController = Service.get[ResourcesController]
  lazy val certificationController: CertificationController = Service.get[CertificationController]

  override def init(context: ServletContext): Unit = {
    logger.info(
      s"swagger_on_path=$swaggerOnPath allowed_cors_headers=($allowedCorsHeaders) allowed_methods=(${CorsConfig.DefaultMethods})"
    )

    context.setInitParameter("org.scalatra.cors.preflightMaxAge", "5")
    context.setInitParameter("org.scalatra.cors.allowCredentials", "false")
    context.setInitParameter("org.scalatra.environment", "production")
    context.setInitParameter("org.scalatra.cors.allowedHeaders", allowedCorsHeaders)
    context.setInitParameter("org.scalatra.cors.allowedMethods", CorsConfig.DefaultMethods)

    context.mount(
      handler = infoController,
      urlPattern = "/",
      name = "Info"
    )

    context.mount(
      handler = resourceController,
      urlPattern = "/api-docs",
      name = "Resources"
    )

    context.mount(
      handler = certificationController,
      urlPattern = "/poc-agent",
      name = "Certification"
    )
  }
}
