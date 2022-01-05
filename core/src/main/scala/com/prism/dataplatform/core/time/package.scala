package com.prism.dataplatform.core

package object time {

  import scala.language.implicitConversions
  implicit def richInstant(instant: Instant): RichInstant = new RichInstant(instant)
  implicit def richDuration(duration: Duration): RichDuration = new RichDuration(duration)
  implicit def richYearMonth(yearMonth: YearMonth): RichYearMonth = new RichYearMonth(yearMonth)
  implicit def richLocalDateTime(ldt: LocalDateTime): RichLocalDateTime = new RichLocalDateTime(ldt)
  implicit def richLocalTime(l: LocalTime): RichLocalTime = new RichLocalTime(l)
  implicit def richLocalDate(ld: LocalDate): RichLocalDate = new RichLocalDate(ld)
  implicit def richZonedDateTime(zdt: ZonedDateTime): RichZonedDateTime = new RichZonedDateTime(zdt)
  implicit def richPeriod(p: Period): RichPeriod = new RichPeriod(p)

  def minimum[T](one: T, another: T)(implicit order: Ordering[T]): T =
    if (order.compare(one, another) <= 0) one else another
  def maximum[T](one: T, another: T)(implicit order: Ordering[T]): T =
    if (order.compare(one, another) <= 0) another else one

  implicit class StringToInstant(val sc: StringContext) {
    def i(args: Any*): Instant = Instant(sc.s(args: _*))
  }

  type Clock = java.time.Clock
  type Month = java.time.Month
  type Year = java.time.Year
  type DayOfWeek = java.time.DayOfWeek
  type LocalTime = java.time.LocalTime
  type Instant = java.time.Instant
  type YearMonth = java.time.YearMonth
  type Duration = java.time.Duration
  type LocalDate = java.time.LocalDate
  type LocalDateTime = java.time.LocalDateTime
  type ZonedDateTime = java.time.ZonedDateTime
  type Period = java.time.Period
  type ZoneId = java.time.ZoneId
  type ZoneOffset = java.time.ZoneOffset
  type TimeUnit = java.util.concurrent.TimeUnit

  type IsoChronology = java.time.chrono.IsoChronology
  type Chronology = java.time.chrono.Chronology

  type DateTimeFormatter = java.time.format.DateTimeFormatter
  type FormatStyle = java.time.format.FormatStyle

  type Temporal = java.time.temporal.Temporal
  type TemporalUnit = java.time.temporal.TemporalUnit
  type TemporalAccessor = java.time.temporal.TemporalAccessor
  type TemporalAmount = java.time.temporal.TemporalAmount
  type ChronoUnit = java.time.temporal.ChronoUnit
}
