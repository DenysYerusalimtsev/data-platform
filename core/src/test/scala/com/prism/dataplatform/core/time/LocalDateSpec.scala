package com.prism.dataplatform.core.time

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import com.prism.dataplatform.core.time.Month._

class LocalDateSpec extends AnyWordSpecLike with Matchers {

  "the local date enrichment" should {

    "create local dates" in {
      LocalDate(2015, 1, 1) should equal(LocalDate(2015, January, 1))
      LocalDate("2015-05-23") should equal(LocalDate(2015, May, 23))
    }

    "calculate period between two dates" in {
      val result = LocalDate(2015, 2, 1) - LocalDate(2015, 1, 1)
      result.getMonths should be(1)
    }

    "unapply" in {
      val result = LocalDate(2015, 1, 2) match {
        case LocalDate(y, m, d) => (y, m, d)
      }
      result should equal(2015, January, 2)
    }

    "add with a period" in {
      val result = LocalDate(2015, 1, 1) + Days(2)
      result should equal(LocalDate(2015, January, 3))
    }

    "subtract with a period" in {
      val result = LocalDate(2015, 1, 1) - Days(2)
      result should equal(LocalDate(2014, December, 30))
    }

    "compare local dates" in {
      LocalDate(2019, 1, 1) >= LocalDate(2015, 1, 1) should equal(true)
    }
  }

}
