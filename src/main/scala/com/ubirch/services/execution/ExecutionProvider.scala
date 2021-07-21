package com.ubirch.services.execution

import com.typesafe.config.Config
import com.ubirch.ConfPaths.ExecutionContextConfPaths
import monix.execution.Scheduler
import java.util.concurrent.Executors

import com.typesafe.scalalogging.LazyLogging

import javax.inject._
import scala.concurrent.{ ExecutionContext, ExecutionContextExecutor }

/**
  * Represents the Execution Context Component used in the system
  */
trait Execution {
  implicit def ec: ExecutionContextExecutor
}

/**
  * Represents the Execution Context provider.
  * Whenever someone injects an ExecutionContext, this provider defines what will
  * be returned.
  * @param config Represents the configuration object
  */
@Singleton
class ExecutionProvider @Inject() (config: Config) extends Provider[ExecutionContext] with Execution with ExecutionContextConfPaths with LazyLogging {

  final val threadPoolSize: Int = config.getInt(THREAD_POOL_SIZE)

  override val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(threadPoolSize))

  logger.info("thead_pool_size=" + threadPoolSize)

  override def get(): ExecutionContext = ec

}

/**
  * Represents a Scheduler used in the system
  */
trait SchedulerBase {
  implicit val scheduler: Scheduler
}

/**
  * Represents the Scheduler Provider
  * @param ec Represents the execution context for async processes.
  */
@Singleton
class SchedulerProvider @Inject() (ec: ExecutionContext) extends Provider[Scheduler] with SchedulerBase {

  override val scheduler: Scheduler = monix.execution.Scheduler(ec)

  override def get(): Scheduler = scheduler

}

@Singleton
class SchedulerIOProvider extends Provider[Scheduler] with SchedulerBase {

  lazy val scheduler: Scheduler = monix.execution.Scheduler.io("certify-io")

  override def get(): Scheduler = scheduler

}

