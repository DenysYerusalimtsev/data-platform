package com.prism.dataplatform.core.time

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class LocalTimeSpec extends AnyWordSpecLike with Matchers {

  "the local time enrichment" should {

    "create local times" in {
      LocalTime(20, 30) shouldEqual LocalTime(20, 30, 0, 0)
    }

    "unapply" in {
      val result = LocalTime(20, 30, 1, 2) match {
        case LocalTime(h, m, s, n) => (h, m, s, n)
      }
      result shouldEqual (20, 30, 1, 2)
    }

    "add with a duration" in {
      val result = LocalTime(20, 30) + Minutes(2)
      result shouldEqual LocalTime(20, 32)
    }

    "subtract with a duration" in {
      val result = LocalTime(20, 32) - Minutes(2)
      result shouldEqual LocalTime(20, 30)
    }

    "should compare" in {
      val first = LocalTime(20, 30)
      val last = LocalTime(20, 31)

      (first < last) shouldBe true
      (last > first) shouldBe true
      (last != first) shouldBe true

      (first min last) shouldBe first
      (first min first) shouldBe first

      (first max last) shouldBe last
      (last max last) shouldBe last
    }
  }
}
