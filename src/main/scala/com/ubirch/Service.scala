package com.ubirch

import com.typesafe.scalalogging.LazyLogging
import com.ubirch.services.execution.HttpClientProvider
import com.ubirch.services.rest.RestService
import monix.eval.Task
import monix.execution.{ CancelableFuture, Scheduler }
import javax.inject.{ Inject, Singleton }

/**
  * Represents a bootable service object that starts the system
  */
@Singleton
class Service @Inject() (
    restService: RestService,
    httpClient: HttpClientProvider
)(implicit scheduler: Scheduler) extends LazyLogging {

  val home: String = System.getProperty("user.home")
  val runtimeVersion: Runtime.Version = Runtime.version

  logger.info(s"service_version=${Service.version} user_home=$home runtime=${runtimeVersion.toString}")

  def start(): CancelableFuture[Unit] = {
    (for {
      _ <- Task.delay(httpClient.init())
      _ <- Task.delay(restService.start())
    } yield ()).onErrorRecover {
      case e: Exception =>
        logger.error("error_starting=" + e.getClass.getCanonicalName + " - " + e.getMessage, e)
        sys.exit(1)
    }.runToFuture
  }

}

object Service extends Boot(List(new Binder)) {
  final val version = "0.1.0"

  def main(args: Array[String]): Unit = * {
    get[Service].start()
  }
}
