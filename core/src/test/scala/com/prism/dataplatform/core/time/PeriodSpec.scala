package com.prism.dataplatform.core.time

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class PeriodSpec extends AnyWordSpecLike with Matchers {

  "the duration enrichment" should {

    "create instances" in {
      val p = Period(years = 1, months = 2, days = 3)
      p.years shouldEqual 1
      p.months shouldEqual 2
      p.days shouldEqual 3

      Days(10).days shouldEqual 10
      Weeks(10).days shouldEqual 70
      Months(10).months shouldEqual 10
      Years(10).years shouldEqual 10
    }

    "add with another duration" in {
      (Days(1) + Days(1)) shouldEqual Days(2)
    }

    "subtract" in {
      (Days(2) - Days(1)) shouldEqual Days(1)
    }

    "multiply" in {
      (Days(10) * 2) shouldEqual Days(20)
    }

    "compare instances" in {
      Days(20) > Days(19) should be(true)
    }

    "transform durations in to scala finite durations" in {
      val finite = Duration(20).toFiniteDuration
      import scala.concurrent.duration._
      finite should equal(20.seconds)
    }

    "between two local date" in {
      val result = Period.between(LocalDate(1, 2, 3), LocalDate(4, 5, 6))
      result should equal(Period(3, 3, 3))
    }

    "unapply" in {
      val result = Period(1, 2, 3) match {
        case Period(year, month, day) => (year, month, day)
      }
      result should equal((1, 2, 3))
    }
  }
}
