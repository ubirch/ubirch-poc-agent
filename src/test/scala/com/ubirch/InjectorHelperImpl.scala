package com.ubirch

import com.google.inject.Singleton
import com.google.inject.binder.ScopedBindingBuilder
import com.ubirch.models.responses.{ SigningResponse, Response => SResponse }
import com.ubirch.services.execution.HttpClientProvider
import org.bouncycastle.util.encoders.Base64
import sttp.client3.testing.{ RecordingSttpBackend, SttpBackendStub }
import sttp.client3.{ Response, ResponseException, SttpBackend }
import sttp.model.{ Header, Method, StatusCode }

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class DefaultTestHttpClient(stubBackend: StubBackend) extends HttpClientProvider {
  override val backend: SttpBackend[Future, Any] = stubBackend.backend

  override def init(): Unit = { () }
}

trait StubBackend {
  val backend: SttpBackend[Future, Any]
}

class StubResource extends StubBackend {
  type Backend = SttpBackendStub[Future, Any]

  private val uuidRegex = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})".r

  lazy val SigningBehaviourResponse: Either[ResponseException[String, Exception], SigningResponse] = Right(
    SigningResponse(
      hash = "PYJCVPY2C9+Wx9AVHgMl9f3oeM5AWbqR7H6q8kVYlyA=",
      upp =
        "liPEEI5qVnlbDEoemqVPbkL/9bPEQEvRcN6FSHNGh6vbvS1clcyPEmRMxcxdenvTSZBs8wF7qH1EEnt1vPIvQ930p4DtGOwzRTXa1klPqtqEeiUiWhgAxCA9gkJU9jYL35bH0BUeAyX1/eh4zkBZupHsfqryRViXIMRAAxJ89aasfPXfdlt7qvpVP4MaYK0cYzhoFrJwzXynajozt3JeC4kjbpRUmTbtgRlE+WA43KIweODR6n+JTgz5WA==",
      response = SResponse(
        statusCode = 200,
        header = Map.empty,
        content =
          "liPEEJ08eP8i80RBpdGFxjbUhv/EQAMSfPWmrHz133Zbe6r6VT+DGmCtHGM4aBaycM18p2o6M7dyXguJI26UVJk27YEZRPlgONyiMHjg0ep/iU4M+VgAxCDX/YVHO1VL+77bn2K+/wLwAAAAAAAAAAAAAAAAAAAAAMRAFihVhd4dDLwRj5haD+Xsis2U8OKYoXPIHE2cZ6c6JMW4lqY0ZdKnM6tr8+5rfrIXgrQlGv6HzfxIxMPewJL2CA=="
      ),
      requestID = Some("d7fd8547-3b55-4bfb-bedb-9f62beff02f0"),
      error = None
    )
  )

  def SigningBehavior(backend: Backend): Backend =
    backend.whenRequestMatches(r =>
      r.uri.path.headOption.exists(x => uuidRegex.matches(x)) && r.method == Method.POST && r.uri.toString.contains(
        DefaultTestHttpClient.GoClientURI
      ))
      .thenRespond(SigningBehaviourResponse)

  lazy val RegisterSealBehaviourResponse: Response[Either[String, Array[Byte]]] = Response(
    Right(Base64.decode("0oRDoQEmoQRI7JQ83GRvK3BZASqkAWJERQQaYuFasAYaYQAnMDkBA6EBpGF0galiY2l4MFVSTjpVVkNJOjAxREUvMTAzNjUwMjAzLzJMMVdQWUcyNTROUVVPWUZNTzBYVk4jOGJjb2JERWJpc3RSb2JlcnQgS29jaC1JbnN0aXR1dGJubXZSb2NoZSBMaWdodEN5Y2xlciBxUENSYnNjdDIwMjEtMDQtMTNUMTQ6MjA6MDBaYnRjckhhdXB0YmFobmhvZiBLw7ZsbmJ0Z2k4NDA1MzkwMDZidHJpMjYwNDE1MDAwYnR0aExQNjQ2NC00Y2RvYmoxOTc5LTA0LTE0Y25hbaRiZm5qTXVzdGVyZnJhdWJnbmVFcmlrYWNmbnRqTVVTVEVSRlJBVWNnbnRlRVJJS0FjdmVyZTEuMy4wWEByBP0xbS6OjgNjsDr4kJhFPfa1uiluqSLVBD2QVu1ETb04oGCN3ViyH/a4EjTTtpC5aEwi5oYH5n+xTvxeZvK3")),
    StatusCode.Ok,
    StatusCode.Ok.toString(),
    Seq(Header("X-DGC-ID", "URN:UVCI:01DE/103650203/2L1WPYG254NQUOYFMO0XVN#8"))
  )

  def RegisterSealBehaviour(backend: Backend): Backend =
    backend.whenRequestMatches(r =>
      r.uri.toString == s"${DefaultTestHttpClient.CertifyURI}/api/certify/v2/issue" && r.method == Method.POST)
      .thenRespond(RegisterSealBehaviourResponse)

  override val backend: SttpBackend[Future, Any] = new RecordingSttpBackend(
    SttpBackendStub
      .asynchronousFuture
      .pipe(SigningBehavior)
      .pipe(RegisterSealBehaviour)
  )
}

object DefaultTestHttpClient {
  val GoClientURI = "http://api.upp.dev.ubirch.com"
  val CertifyURI = "http://api.certify.dev.ubirch.com"
}

class InjectorHelperImpl(stubBackend: StubBackend)
  extends InjectorHelper(List(new Binder {

    override def HttpClientProvider: ScopedBindingBuilder = {
      bind(classOf[HttpClientProvider]).toConstructor(
        classOf[DefaultTestHttpClient].getConstructor(classOf[StubBackend])
      )
    }

    override def configure(): Unit = {
      super.configure()
      bind(classOf[StubBackend]).toInstance(stubBackend)
    }

  }))
