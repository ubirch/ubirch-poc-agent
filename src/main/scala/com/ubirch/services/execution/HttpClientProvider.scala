package com.ubirch.services.execution

import java.io.FileInputStream
import java.security.{ KeyStore, Security }

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.ubirch.ConfPaths.HttpClientConfPaths
import com.ubirch.services.lifecycle.Lifecycle
import io.netty.handler.ssl.{ SslContext, SslContextBuilder }
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import org.bouncycastle.jce.provider.BouncyCastleProvider
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend
import javax.inject.{ Inject, Singleton }
import javax.net.ssl.KeyManagerFactory

import scala.concurrent.Future

trait SSLHttpClientProvider {
  def init(): Unit
  def backend: SttpBackend[Future, Any]
}

@Singleton
class DefaultSSLHttpClient @Inject() (config: Config, lifecycle: Lifecycle) extends SSLHttpClientProvider with LazyLogging {

  val SECURITY_PROVIDER_NAME: String = BouncyCastleProvider.PROVIDER_NAME
  Security.addProvider(new BouncyCastleProvider)

  override def init(): Unit = {
    getContext
    ()
  }

  private lazy val getContext: SslContext = {
    val keystoreType = config.getString(HttpClientConfPaths.HTTP_SSL_CONTEXT_KEYSTORE_TYPE)
    val keyStorePathAndName = config.getString(HttpClientConfPaths.HTTP_SSL_CONTEXT_KEYSTORE)
    val keyStorePassword = config.getString(HttpClientConfPaths.HTTP_SSL_CONTEXT_KEYSTORE_PASSWORD)
    val keyPassword = config.getString(HttpClientConfPaths.HTTP_SSL_CONTEXT_KEY_PASSWORD)

    val ks: KeyStore = KeyStore.getInstance(keystoreType, SECURITY_PROVIDER_NAME)
    ks.load(new FileInputStream(keyStorePathAndName), keyStorePassword.toCharArray)

    val kmf: KeyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    kmf.init(ks, keyPassword.toCharArray)
    SslContextBuilder.forClient().keyManager(kmf).build()
  }

  private final val httpClientConfig: DefaultAsyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder()
    .setSslContext(getContext)
    .build()

  override val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend.usingConfig(httpClientConfig)

  lifecycle.addStopHook { () =>
    Future.successful {
      logger.info("Shutting down Mutual Http Client")
      val _ = backend.close()
    }
  }

}

trait HttpClientProvider {
  def backend: SttpBackend[Future, Any]
}

@Singleton
class DefaultFutureHttpClient @Inject() (lifecycle: Lifecycle) extends HttpClientProvider with LazyLogging {

  override val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

  lifecycle.addStopHook { () =>
    Future.successful {
      logger.info("Shutting down Http Client")
      val _ = backend.close()
    }
  }

}
