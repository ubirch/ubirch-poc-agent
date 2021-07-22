package com.ubirch.models.requests

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UPPSigningRequestTest extends AnyWordSpec with Matchers {

  "UPPSigningRequest" should {
    "be correctly mapped from CertificationRequest" in {
      val certificationRequest = CertificationRequest(
        Name(
          "firstName",
          "givenName"
        ),
        "1979-04-14",
        List(
          Test(
            id = "TC12345",
            se = "2fe00c151cb726bb9ed7",
            tg = "840539006",
            tt = "LP6464-4",
            nm = "Roche LightCycler qPCR",
            tr = "260415000",
            sc = "2021-04-13T14:20:00+00:00",
            tc = "Hauptbahnhof Köln"
          ),
          Test(
            id = "TC67890",
            se = "1ed99b040bc615ac8fc8",
            tg = "987654321",
            tt = "LP6464-5",
            nm = "Roche LightCycler qPCR-2",
            tr = "260415012",
            sc = "2021-04-13T14:22:00+00:00",
            tc = "Hauptbahnhof Köln-2"
          )
        )
      )

      val uppSigningRequest = UPPSigningRequest.fromCertificationRequest(certificationRequest)

      uppSigningRequest shouldBe List(
        UPPSigningRequest(
          f = "firstName",
          g = "givenName",
          b = "1979-04-14",
          p = "Hauptbahnhof Köln",
          i = "TC12345",
          d = "2021-04-13T14:20:00+00:00",
          t = "LP6464-4",
          r = "260415000",
          s = "2fe00c151cb726bb9ed7"
        ),
        UPPSigningRequest(
          f = "firstName",
          g = "givenName",
          b = "1979-04-14",
          p = "Hauptbahnhof Köln-2",
          i = "TC67890",
          d = "2021-04-13T14:22:00+00:00",
          t = "LP6464-5",
          r = "260415012",
          s = "1ed99b040bc615ac8fc8"
        )
      )
    }
  }

}
