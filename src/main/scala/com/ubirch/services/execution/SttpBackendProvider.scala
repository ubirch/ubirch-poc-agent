package com.ubirch.services.execution

import org.asynchttpclient.DefaultAsyncHttpClientConfig
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import javax.inject.Singleton
import scala.concurrent.Future

trait SttpSSLBackendProvider {
  def backend: SttpBackend[Future, Any]
}

@Singleton
class SttpSSLBackend extends SttpSSLBackendProvider {

  val config = new DefaultAsyncHttpClientConfig.Builder().build() // TODO: Add SSL here

  /**
   * This is one single sttp backend with Future and SSL
   *
   * @Important: when you call a http request with Future, this backend object has to be used.
   */
  override val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend.usingConfig(config)
}

trait SttpBackendProvider {
  def backend: SttpBackend[Future, Any]
}

@Singleton
class FutureSttpBackend extends SttpBackendProvider {
  /**
   * This is one single sttp backend with Future
   *
   * @Important: when you call a http request with Future, this backend object has to be used.
   */
  override val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()
}
