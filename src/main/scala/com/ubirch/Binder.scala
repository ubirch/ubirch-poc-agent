package com.ubirch

import com.google.inject.binder.ScopedBindingBuilder
import com.google.inject.name.Names
import com.google.inject.{ AbstractModule, Module }
import com.typesafe.config.Config
import com.ubirch.services.config.ConfigProvider
import com.ubirch.services.execution.{ ExecutionProvider, SchedulerIOProvider, SchedulerProvider }
import com.ubirch.services.formats.JsonFormatsProvider
import com.ubirch.services.lifecycle.{ DefaultJVMHook, DefaultLifecycle, JVMHook, Lifecycle }
import com.ubirch.services.rest.SwaggerProvider
import monix.execution.Scheduler
import org.json4s.Formats
import org.scalatra.swagger.Swagger

import scala.concurrent.ExecutionContext

/**
  * Represents the default binder for the system components
  */
class Binder
  extends AbstractModule {

  def Config: ScopedBindingBuilder = bind(classOf[Config]).toProvider(classOf[ConfigProvider])

  def ExecutionContext: ScopedBindingBuilder = bind(classOf[ExecutionContext]).toProvider(classOf[ExecutionProvider])

  def Scheduler: ScopedBindingBuilder = bind(classOf[Scheduler]).toProvider(classOf[SchedulerProvider])

  def SchedulerIO: ScopedBindingBuilder = bind(classOf[Scheduler]).annotatedWith(Names.named("io")).toProvider(classOf[SchedulerIOProvider])

  def Swagger: ScopedBindingBuilder = bind(classOf[Swagger]).toProvider(classOf[SwaggerProvider])

  def Formats: ScopedBindingBuilder = bind(classOf[Formats]).toProvider(classOf[JsonFormatsProvider])

  def Lifecycle: ScopedBindingBuilder = bind(classOf[Lifecycle]).to(classOf[DefaultLifecycle])

  def JVMHook: ScopedBindingBuilder = bind(classOf[JVMHook]).to(classOf[DefaultJVMHook])

  override def configure(): Unit = {
    Config
    ExecutionContext
    Scheduler
    SchedulerIO
    Swagger
    Formats
    Lifecycle
    JVMHook
    ()
  }

}

object Binder {
  def modules: List[Module] = List(new Binder)
}
