package com.prism.dataplatform.core.time

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import com.prism.dataplatform.core.time.Month._

class YearMonthSpec extends AnyWordSpecLike with Matchers {

  "the year month enrichment" should {

    "create a year month instance" in {
      val instance = YearMonth(2015, 1)
      instance.year shouldEqual 2015
      instance.month shouldEqual January
    }

    "unapply" in {
      val result = YearMonth(2015, January) match {
        case YearMonth(y, m) => (y, m)
      }
      result should equal(2015, January)
    }

    "add with a period" in {
      val result = YearMonth(2015, 1) + Months(1)
      result should equal(YearMonth(2015, 2))
    }

    "subtract with a period" in {
      val result = YearMonth(2015, 1) - Months(1)
      result should equal(YearMonth(2014, 12))
    }

    "compare YearMonth" in {
      YearMonth(2015, 1) <= YearMonth(2019, 1) should equal(true)
    }
  }
}
