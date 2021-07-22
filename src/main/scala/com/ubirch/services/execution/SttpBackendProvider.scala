package com.ubirch.services.execution

import java.io.FileInputStream
import java.security.KeyStore

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.ConfPaths.HttpClientConfPaths
import io.netty.handler.ssl.{ SslContext, SslContextBuilder }
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import javax.inject.{ Inject, Singleton }
import javax.net.ssl.KeyManagerFactory
import scala.concurrent.Future

trait SttpSSLBackendProvider {
  def backend: SttpBackend[Future, Any]
}

@Singleton
class DefaultSttpSSLBackend @Inject() (config: Config) extends SttpSSLBackendProvider with LazyLogging {

  private final val keyStorePathAndName = config.getString(HttpClientConfPaths.HTTP_SSL_CONTEXT_KEYSTORE)
  private final val keyStorePassword = config.getString(HttpClientConfPaths.HTTP_SSL_CONTEXT_KEYSTORE_PASSWORD)
  private final val privateKeyPassword = config.getString(HttpClientConfPaths.HTTP_SSL_CONTEXT_PRIVATE_KEY_PASSWORD)

  private final val ks: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType)
  ks.load(new FileInputStream(keyStorePathAndName), keyStorePassword.toCharArray)

  private final val kmf: KeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
  kmf.init(ks, privateKeyPassword.toCharArray)

  private final val sslContext: SslContext = SslContextBuilder.forClient().keyManager(kmf).build()

  private final val httpClientConfig: DefaultAsyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder()
    .setSslContext(sslContext)
    .build()

  override val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend.usingConfig(httpClientConfig)

  sys.addShutdownHook {
    logger.info("Shutting down Mutual Http Client")
    val _ = backend.close()
  }

}

trait SttpBackendProvider {
  def backend: SttpBackend[Future, Any]
}

@Singleton
class DefaultFutureSttpBackend extends SttpBackendProvider with LazyLogging {
  /**
    * This is one single sttp backend with Future
    *
    * @Important: when you call a http request with Future, this backend object has to be used.
    */
  override val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

  sys.addShutdownHook {
    logger.info("Shutting down Http Client")
    val _ = backend.close()
  }

}
