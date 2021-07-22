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
    final val HTTP_SSL_CONTEXT_KEYSTORE_TYPE = "system.http.client.sslKeyStoreType"
    final val HTTP_SSL_CONTEXT_KEYSTORE = "system.http.client.sslKeyStore"
    final val HTTP_SSL_CONTEXT_KEYSTORE_PASSWORD = "system.http.client.sslKeyStorePassword"
    final val HTTP_SSL_CONTEXT_KEY_PASSWORD = "system.http.client.keyPassword"
  }

  trait ExecutionContextConfPaths {
    final val THREAD_POOL_SIZE = "system.executionContext.threadPoolSize"
  }

  trait PrometheusConfPaths {
    final val PORT = "system.metrics.prometheus.port"
  }

  trait UppSigningPaths {
    final val ENDPOINT = "system.upp.endpoint"
    final val DEVICE_ID = "system.upp.signing.deviceId"
    final val DEVICE_PWD = "system.upp.signing.devicePwd"
  }

  trait CertifyPaths {
    final val ENDPOINT = "system.certify.endpoint"
  }

  object GenericConfPaths extends GenericConfPaths
  object HttpServerConfPaths extends HttpServerConfPaths
  object HttpClientConfPaths extends HttpClientConfPaths
  object UppSigningPaths extends UppSigningPaths
  object CertifyPaths extends CertifyPaths
}
