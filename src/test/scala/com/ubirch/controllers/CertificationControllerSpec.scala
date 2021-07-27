package com.ubirch.controllers

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

  //TODO: Implement missing tests

  "Certification Controller" must {
    "generate proper upp-dcc response" in {
      succeed
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
    "response headers include dcc id header and request id header" in {
      succeed
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
