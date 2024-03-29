package com.prism.dataplatform.core.time

/** Provides implicit decoration for ints and longs so that durations and periods can be created
 * much like the `scala.concurrent.duration` mini-dsl.
 *
 * Example:
 * {{{
 *   import com.cnh.core.time.expr._
 *   val duration = 5 seconds
 *   val period = 1 week
 * }}}
 */
object expr {
  implicit final class RichInt(val n: Int) extends AnyVal {
    def hour: Duration = Hours(n)
    def hours: Duration = Hours(n)
    def minute: Duration = Minutes(n)
    def minutes: Duration = Minutes(n)
    def second: Duration = Seconds(n)
    def seconds: Duration = Seconds(n)
    def milli: Duration = Millis(n)
    def millis: Duration = Millis(n)
    def nano: Duration = Nanos(n)
    def nanos: Duration = Nanos(n)
    def day: Period = Days(n)
    def days: Period = Days(n)
    def week: Period = Weeks(n)
    def weeks: Period = Weeks(n)
    def month: Period = Months(n)
    def months: Period = Months(n)
    def year: Period = Years(n)
    def years: Period = Years(n)
  }
}
