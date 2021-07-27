package com.ubirch.services.config

import com.typesafe.config.{ Config, ConfigFactory }

import javax.inject._

/**
  * Configuration Provider for the Configuration Component.
  */
@Singleton
class ConfigProvider extends Provider[Config] {

  private val default: Config = ConfigFactory.load()

  private def conf: Config = default

  override def get(): Config = conf

}
