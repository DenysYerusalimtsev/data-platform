package com.prism.dataplatform.core.time

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import com.prism.dataplatform.core.time.Month._

class LocalDateTimeSpec extends AnyWordSpecLike with Matchers {

  "the local date enrichment" should {

    "create local dates" in {
      LocalDateTime("2015-05-23T20:30:05") should equal(LocalDateTime(2015, May, 23, 20, 30, 5))
    }

    "add a period" in {
      val result = LocalDateTime(LocalDate(2015, 1, 1), LocalTime(20, 30, 5)) + Days(2)
      result shouldEqual LocalDateTime(2015, January, 3, 20, 30, 5)
    }

    "subtract a period" in {
      val result = LocalDateTime(2015, 1, 1, 20, 30, 5) - Days(2)
      result shouldEqual LocalDateTime(2014, December, 30, 20, 30, 5)
    }

    "add a duration" in {
      val result = LocalDateTime(2015, 1, 1, 20, 30, 0) + Hours(2)
      result shouldEqual LocalDateTime(2015, January, 1, 22, 30)
    }

    "subtract a duration" in {
      val result = LocalDateTime(2015, 1, 1, 20, 30, 5) - Hours(2)
      result shouldEqual LocalDateTime(2015, January, 1, 18, 30, 5)
    }

    "create a duration between two date times" in {
      val result = LocalDateTime("2015-05-24T22:30:25") - LocalDateTime("2015-05-23T20:30:05")
      result.seconds shouldEqual (26 * 60 * 60 + 20)
    }

    "compare local date times" in {
      LocalDateTime(2015, 1, 1, 23, 0, 0) >= LocalDateTime(2015, 1, 1, 12, 0, 0) shouldEqual true
    }
  }
}
