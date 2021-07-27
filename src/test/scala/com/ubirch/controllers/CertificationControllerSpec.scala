package com.ubirch.controllers

import com.ubirch.{ Awaits, ExecutionContextsTests, InjectorHelperImpl }
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
      succeed
    }
    "fail when the device id is not a uuid" in {
      succeed
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
