package com.ubirch.controllers

import java.nio.charset.StandardCharsets
import java.util.UUID

import com.ubirch.{ Awaits, ExecutionContextsTests, InjectorHelperImpl, Service }
import io.prometheus.client.CollectorRegistry
import org.scalatest.BeforeAndAfterEach
import org.scalatra.test.scalatest.ScalatraWordSpec

class CertificationControllerSpec
  extends ScalatraWordSpec
  with BeforeAndAfterEach
  with ExecutionContextsTests
  with Awaits {

  lazy val Injector: InjectorHelperImpl = new InjectorHelperImpl() {}

  "Certification Controller" must {

    "generate proper upp-dcc response" in {

      val device = "8e6a5679-5b0c-4a1e-9aa5-4f6e42fff5b3"
      val data =
        s"""
           |{
           |  "nam": {
           |    "fn": "Musterfrau",
           |    "gn": "Erika"
           |  },
           |  "dob": "1979-04-14",
           |  "t": [
           |    {
           |      "id": "103650203",
           |      "tg": "840539006",
           |      "tt": "LP6464-4",
           |      "nm": "Roche LightCycler qPCR",
           |      "tr": "260415000",
           |      "sc": "2021-04-13T14:20:00+00:00",
           |      "dr": "2021-04-13T20:00:01+00:00",
           |      "tc": "Hauptbahnhof Köln",
           |      "se": "1234567890"
           |    }
           |  ]
           |}
          |""".stripMargin

      val expected =
        """{"hash":"PYJCVPY2C9+Wx9AVHgMl9f3oeM5AWbqR7H6q8kVYlyA=","upp":"liPEEI5qVnlbDEoemqVPbkL/9bPEQEvRcN6FSHNGh6vbvS1clcyPEmRMxcxdenvTSZBs8wF7qH1EEnt1vPIvQ930p4DtGOwzRTXa1klPqtqEeiUiWhgAxCA9gkJU9jYL35bH0BUeAyX1/eh4zkBZupHsfqryRViXIMRAAxJ89aasfPXfdlt7qvpVP4MaYK0cYzhoFrJwzXynajozt3JeC4kjbpRUmTbtgRlE+WA43KIweODR6n+JTgz5WA==","dcc":"0oRDoQEmoQRI7JQ83GRvK3BZASqkAWJERQQaYuFasAYaYQAnMDkBA6EBpGF0galiY2l4MFVSTjpVVkNJOjAxREUvMTAzNjUwMjAzLzJMMVdQWUcyNTROUVVPWUZNTzBYVk4jOGJjb2JERWJpc3RSb2JlcnQgS29jaC1JbnN0aXR1dGJubXZSb2NoZSBMaWdodEN5Y2xlciBxUENSYnNjdDIwMjEtMDQtMTNUMTQ6MjA6MDBaYnRjckhhdXB0YmFobmhvZiBLw7ZsbmJ0Z2k4NDA1MzkwMDZidHJpMjYwNDE1MDAwYnR0aExQNjQ2NC00Y2RvYmoxOTc5LTA0LTE0Y25hbaRiZm5qTXVzdGVyZnJhdWJnbmVFcmlrYWNmbnRqTVVTVEVSRlJBVWNnbnRlRVJJS0FjdmVyZTEuMy4wWEByBP0xbS6OjgNjsDr4kJhFPfa1uiluqSLVBD2QVu1ETb04oGCN3ViyH/a4EjTTtpC5aEwi5oYH5n+xTvxeZvK3","response":{"statusCode":200,"header":{},"content":"liPEEJ08eP8i80RBpdGFxjbUhv/EQAMSfPWmrHz133Zbe6r6VT+DGmCtHGM4aBaycM18p2o6M7dyXguJI26UVJk27YEZRPlgONyiMHjg0ep/iU4M+VgAxCDX/YVHO1VL+77bn2K+/wLwAAAAAAAAAAAAAAAAAAAAAMRAFihVhd4dDLwRj5haD+Xsis2U8OKYoXPIHE2cZ6c6JMW4lqY0ZdKnM6tr8+5rfrIXgrQlGv6HzfxIxMPewJL2CA=="},"requestID":"d7fd8547-3b55-4bfb-bedb-9f62beff02f0","dccID":"URN:UVCI:01DE/103650203/2L1WPYG254NQUOYFMO0XVN#8"}""".stripMargin

      post("/" + device, data.getBytes(StandardCharsets.UTF_8), Map("X-Auth-Token" -> UUID.randomUUID().toString)) {
        assert(status == 200)
        assert(body == expected)
        assert(header.get("X-DGC-ID") == Some("URN:UVCI:01DE/103650203/2L1WPYG254NQUOYFMO0XVN#8"))
        assert(header.get("requestID") == Some("d7fd8547-3b55-4bfb-bedb-9f62beff02f0"))
      }
    }
    "fail when no auth-token is provided" in {
      val expectedBody = s"""{"version":"${Service.version}","ok":false,"data":"Sorry, there is something invalid in your request: Header is missing (X-Auth-Token)"}"""
      val device = "8e6a5679-5b0c-4a1e-9aa5-4f6e42fff5b3"
      post("/" + device) {
        assert(status == 400)
        assert(body == expectedBody)
      }
    }
    "fail when the device id is not a uuid" in {
      val expectedBody = s"""{"version":"${Service.version}","ok":false,"data":"Sorry, there is something invalid in your request: Expected deviceId in UUID format"}""".stripMargin
      val device = "this_is_not_a_uuid"
      post("/" + device, Array.empty[Byte], Map("X-Auth-Token" -> "this is a token")) {
        assert(status == 400)
        assert(body == expectedBody)
      }
    }
  }

  override protected def beforeEach(): Unit = {
    CollectorRegistry.defaultRegistry.clear()
  }

  protected override def afterAll(): Unit = {
    super.afterAll()
  }

  protected override def beforeAll(): Unit = {

    lazy val controller = Injector.get[CertificationController]
    addServlet(controller, "")
    super.beforeAll()
  }

}
