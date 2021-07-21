package com.ubirch

/**
  * Object that contains configuration keys
  */
object ConfPaths {

  trait GenericConfPaths {
    final val NAME = "system.name"
  }

  trait HttpServerConfPaths {
    final val PORT = "system.server.port"
    final val SWAGGER_PATH = "system.server.swaggerPath"
    final val SWAGGER_ACTIVATED_PATH = "system.server.swaggerActivated"
    final val GENERAL_ADDITIONAL_CORS_HEADERS = "system.server.additionalAllowedHeaders"
  }

  trait HttpClientConfPaths {
    final val HTTP_POOL_DEFAULT = "system.http.client.httpPoolDefault"
    final val HTTP_POOL_MAX = "system.http.client.httpPoolMax"
    final val HTTP_DEFAULT_MAX_PER_ROUTE = "system.http.client.httpDefaultMaxPerRoute"
    final val TIMEOUT = "system.http.client.timeout"
    final val HTTP_CONN_TIME_TO_LIVE = "system.http.client.timeToLive"
  }

  trait ExecutionContextConfPaths {
    final val THREAD_POOL_SIZE = "system.executionContext.threadPoolSize"
  }

  trait PrometheusConfPaths {
    final val PORT = "system.metrics.prometheus.port"
  }

  object GenericConfPaths extends GenericConfPaths
  object HttpServerConfPaths extends HttpServerConfPaths
  object HttpClientConfPaths extends HttpClientConfPaths
}
