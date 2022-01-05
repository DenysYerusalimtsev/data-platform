package com.prism.dataplatform.core.time

import java.time.temporal.ChronoUnit
import scala.concurrent.duration.FiniteDuration

final class RichLocalDateTime private[time] (l: LocalDateTime) extends Ordered[LocalDateTime] {
  def year: Int = l.getYear
  def dayOfMonth: Int = l.getDayOfMonth
  def month: Month = l.getMonth
  def monthValue: Int = l.getMonthValue
  def dayOfWeek: DayOfWeek = l.getDayOfWeek
  def dayOfYear: Int = l.getDayOfYear
  def hour: Int = l.getHour
  def minute: Int = l.getMinute
  def second: Int = l.getSecond
  def nano: Int = l.getNano
  def chronology: Chronology = l.getChronology

  def +(amount: Period): LocalDateTime = l.plus(amount)
  def -(amount: Period): LocalDateTime = l.minus(amount)
  def +(amount: Duration): LocalDateTime = l.plus(amount)
  def -(amount: Duration): LocalDateTime = l.minus(amount)

  def +(amount: FiniteDuration): LocalDateTime = l.plus(amount.toMillis, ChronoUnit.MILLIS)
  def -(amount: FiniteDuration): LocalDateTime = l.minus(amount.toMillis, ChronoUnit.MILLIS)

  def -(other: LocalDateTime): Duration = Duration.between(other, l)

  override def compare(that: LocalDateTime): Int = l.compareTo(that)

  def min(other: LocalDateTime): LocalDateTime = minimum(l, other)
  def max(other: LocalDateTime): LocalDateTime = maximum(l, other)
}

final class RichLocalDate private[time] (l: LocalDate) extends Ordered[LocalDate] {
  def year: Int = l.getYear
  def dayOfMonth: Int = l.getDayOfMonth
  def month: Month = l.getMonth
  def monthValue: Int = l.getMonthValue
  def dayOfWeek: DayOfWeek = l.getDayOfWeek
  def dayOfYear: Int = l.getDayOfYear
  def chronology: Chronology = l.getChronology

  def +(amount: Period): LocalDate = l.plus(amount)
  def -(amount: Period): LocalDate = l.minus(amount)

  def +(amount: FiniteDuration): LocalDate = l.plus(amount.toDays, ChronoUnit.DAYS)
  def -(amount: FiniteDuration): LocalDate = l.minus(amount.toDays, ChronoUnit.DAYS)

  /** @return the period from other date until this date, exclusive end date
   * @see java.time.LocalDate.until(ChronoLocalDate)
   */
  def -(other: LocalDate): Period = other.until(l)

  override def compare(that: LocalDate): Int = l.compareTo(that)

  def min(other: LocalDate): LocalDate = minimum(l, other)
  def max(other: LocalDate): LocalDate = maximum(l, other)

  def daysTo(other: LocalDate): Seq[LocalDate] =
    (l.toEpochDay to other.toEpochDay).map(LocalDate(_))
  def daysUntil(other: LocalDate): Seq[LocalDate] =
    (l.toEpochDay until other.toEpochDay).map(LocalDate(_))
}

final class RichLocalTime private[time] (l: LocalTime) extends Ordered[LocalTime] {
  def hour: Int = l.getHour
  def minute: Int = l.getMinute
  def second: Int = l.getSecond
  def nano: Int = l.getNano

  def +(duration: Duration): LocalTime = l.plus(duration)
  def -(duration: Duration): LocalTime = l.minus(duration)

  def +(duration: FiniteDuration): LocalTime = l.plus(duration.toMillis, ChronoUnit.MILLIS)
  def -(duration: FiniteDuration): LocalTime = l.minus(duration.toMillis, ChronoUnit.MILLIS)

  override def compare(that: LocalTime): Int = l.compareTo(that)

  def min(other: LocalTime): LocalTime = minimum(l, other)
  def max(other: LocalTime): LocalTime = maximum(l, other)
}

final class RichZonedDateTime private[time] (z: ZonedDateTime) extends Ordered[ZonedDateTime] {
  def year: Int = z.getYear
  def dayOfMonth: Int = z.getDayOfMonth
  def month: Month = z.getMonth
  def monthValue: Int = z.getMonthValue
  def dayOfWeek: DayOfWeek = z.getDayOfWeek
  def dayOfYear: Int = z.getDayOfYear
  def hour: Int = z.getHour
  def minute: Int = z.getMinute
  def second: Int = z.getSecond
  def nano: Int = z.getNano
  def zone: ZoneId = z.getZone
  def offset: ZoneOffset = z.getOffset

  def localDate: LocalDate = z.toLocalDate
  def localTime: LocalTime = z.toLocalTime
  def localDateTime: LocalDateTime = z.toLocalDateTime

  def +(amount: Period): ZonedDateTime = z.plus(amount)
  def -(amount: Period): ZonedDateTime = z.minus(amount)
  def +(amount: Duration): ZonedDateTime = z.plus(amount)
  def -(amount: Duration): ZonedDateTime = z.minus(amount)

  def +(amount: FiniteDuration): ZonedDateTime = z.plus(amount.toMillis, ChronoUnit.MILLIS)
  def -(amount: FiniteDuration): ZonedDateTime = z.minus(amount.toMillis, ChronoUnit.MILLIS)

  def -(other: ZonedDateTime): Duration = Duration.between(other, z)

  def chronology: Chronology = z.getChronology
  override def compare(other: ZonedDateTime): Int = z.compareTo(other)
}

final class RichInstant private[time] (i: Instant) extends Ordered[Instant] {
  def nano: Int = i.getNano
  def epochSecond: Long = i.getEpochSecond
  override def compare(that: Instant): Int = i.compareTo(that)

  def -(other: Instant): Duration = Duration.between(other, i)
  def -(amount: TemporalAmount): Instant = i.minus(amount)
  def -(amount: FiniteDuration): Instant = i.minus(amount.toMillis, ChronoUnit.MILLIS)
  def -(amount: Long, unit: TemporalUnit): Instant = i.minus(amount, unit)

  def +(amount: TemporalAmount): Instant = i.plus(amount)
  def +(amount: FiniteDuration): Instant = i.plus(amount.toMillis, ChronoUnit.MILLIS)
  def +(amount: Long, unit: TemporalUnit): Instant = i.plus(amount, unit)

  def min(other: Instant): Instant = minimum(i, other)
  def max(other: Instant): Instant = maximum(i, other)
}

final class RichDuration private[time] (d: Duration) extends Ordered[Duration] {

  def nanos: Long = d.toNanos
  def millis: Long = d.toMillis
  def seconds: Long = d.getSeconds
  def minutes: Long = d.toMinutes
  def hours: Long = d.toHours
  def days: Long = d.toDays

  def -(other: Duration): Duration = d.minus(other)
  def +(other: Duration): Duration = d.plus(other)
  def /(divisor: Long): Duration = d.dividedBy(divisor)
  def *(scalar: Long): Duration = d.multipliedBy(scalar)

  def toFiniteDuration: FiniteDuration = {
    val seconds = FiniteDuration(d.getSeconds, TimeUnit.Seconds)
    val nanos = d.getNano
    if (nanos == 0) seconds
    else seconds + FiniteDuration(d.getNano, TimeUnit.NanoSeconds)
  }

  override def compare(other: Duration): Int = d.compareTo(other)

  def to(unit: TemporalUnit): Double = millis.toDouble / unit.getDuration.toMillis

  def min(other: Duration): Duration = minimum(d, other)
  def max(other: Duration): Duration = maximum(d, other)
}

final class RichPeriod private[time] (p: Period) extends Ordered[Period] {

  def days: Int = p.getDays
  def months: Int = p.getMonths
  def years: Int = p.getYears
  def chronology: IsoChronology = p.getChronology

  def -(other: TemporalAmount): Period = p.minus(other)
  def +(other: TemporalAmount): Period = p.plus(other)
  def *(scalar: Int): Period = p.multipliedBy(scalar)

  override def compare(that: Period): Int = p.minus(that).getDays
}

final class RichYearMonth private[time] (ym: YearMonth) extends Ordered[YearMonth] {
  def year: Int = ym.getYear
  def month: Month = ym.getMonth
  def monthValue: Int = ym.getMonthValue

  def -(amount: Period): YearMonth = ym.minus(amount)
  def +(amount: Period): YearMonth = ym.plus(amount)

  override def compare(other: YearMonth): Int = ym.compareTo(other)
}
