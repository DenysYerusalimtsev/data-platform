package com.prism.dataplatform.core.time

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.DurationInt

class InstantSpec extends AnyWordSpecLike with Matchers {

  "instant enrichment" should {

    "add a finite duration" in {
      val result = Instant("2021-05-01T10:00:00Z") + 5.hours
      result shouldEqual Instant("2021-05-01T15:00:00Z")
    }

    "subtract a finite duration" in {
      val result = Instant("2021-05-01T10:00:00Z") - 5.hours
      result shouldEqual Instant("2021-05-01T05:00:00Z")
    }

    "support string interpolation" in {
      i"2021-05-01T10:00:00Z" shouldEqual Instant("2021-05-01T10:00:00Z")
    }
  }
}
