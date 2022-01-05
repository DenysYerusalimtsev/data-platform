package com.prism.dataplatform.core.config

import cats.implicits._
import com.prism.dataplatform.core.BaseTest
import com.prism.dataplatform.core.config.Samples._
import com.prism.dataplatform.core.config.Reader._
import com.prism.dataplatform.core.time.{Duration, Instant, StringToInstant}

import java.time.temporal.ChronoUnit
import scala.collection.immutable.Seq

class ReaderSpec extends BaseTest {

  "Reader" should "handle strings" in {
    Reader[String].read(StringValue("abc")) shouldBe "abc".valid
  }

  "Reader" should "handle strings from seq" in {
    Reader[String].read(StringValue("abc,def")) shouldBe "abc,def".valid
  }

  "Reader" should "handle ints" in {
    Reader[Int].read(StringValue("42")) shouldBe 42.valid
  }

  "Reader" should "handle booleans" in {
    Reader[Boolean].read(StringValue("false")) shouldBe false.valid
  }

  "Reader" should "handle instants" in {
    Reader[Instant].read(
      StringValue("2021-01-01T00:00:00Z")) shouldBe i"2021-01-01T00:00:00Z".valid
  }

  "Reader" should "handle durations" in {
    Reader[Duration].read(StringValue("2 days")) shouldBe Duration(2, ChronoUnit.DAYS).valid
  }

  "Reader" should "handle present option value" in {
    Reader[Option[String]].read(StringValue("bruh")) shouldBe Some("bruh").valid
  }

  "Reader" should "handle missing option value" in {
    Reader[Option[String]].read(MissingValue()) shouldBe None.valid
  }

  "Reader" should "handle lists" in {
    Reader[Seq[Int]].read(StringValue("1,2,3")) shouldBe Seq(1, 2, 3).valid
  }

  "Reader" should "handle single element list" in {
    Reader[Seq[Instant]].read(StringValue("2021-01-01T00:00:00Z")) shouldBe Seq(
      i"2021-01-01T00:00:00Z").valid
  }

  "Reader" should "handle empty list" in {
    Reader[Seq[String]].read(MissingValue()) shouldBe Nil.valid
  }

  "Reader" should "handle maps" in {
    Reader[Map[String, Int]].read(ProductValue(
      Map("a" -> StringValue("1", Seq("a")), "b" -> StringValue("2", Seq("b"))))) shouldBe Map(
      "a" -> 1,
      "b" -> 2).valid
  }

  "Reader" should "handle empty maps" in {
    Reader[Map[String, Instant]].read(MissingValue()) shouldBe Map().valid
  }

  "Reader" should "handle enums" in {
    val r = Reader[Mode]
//
//    val w = Reader[Mode].read(StringValue("write"))
//    w shouldBe Write.valid
  }

//    "Reader" should "handle full config" in {
//      val tree = ProductValue(
//        Map(
//          "a" -> StringValue("42", Seq("a")),
//          "b" -> StringValue("test", Seq("b")),
//          "c" -> ProductValue(
//            Map(
//              "d" -> StringValue("13.5", Seq("c", "d")),
//              "e" -> ProductValue(
//                Map("f" -> StringValue("a=1,b=2", Seq("c", "e", "f"))),
//                Seq("c", "e"))),
//            Seq("c"))))
//
//      Reader[Config].read(tree) shouldBe Config(
//        a = 42,
//        b = Some("test"),
//        c = Nested(d = 13.5, e = Deep(f = Map("a" -> "1", "b" -> "2")))).valid
//    }
  //
  //  "Reader" should "handle partial config" in {
  //    val tree = ProductValue(
  //      Map(
  //        "a" -> StringValue("42", Seq("a")),
  //        "c" -> ProductValue(
  //          Map(
  //            "d" -> StringValue("13.5", Seq("c", "d")),
  //            "e" -> ProductValue(
  //              Map("f" -> StringValue("a=1,b=2", Seq("c", "e", "f"))),
  //              Seq("c", "e"))),
  //          Seq("c"))))
  //
  //    Reader[Config].read(tree) shouldBe Config(
  //      a = 42,
  //      b = None,
  //      c = Nested(d = 13.5, e = Deep(f = Map("a" -> "1", "b" -> "2")))).valid
  //  }
  //
  //  "Reader" should "handle custom readers" in {
  //    implicit val modeReader: Reader[Mode] = Reader.parse {
  //      case "write" => Write
  //      case "read" => Read
  //    }
  //    val cfg = Reader[Cfg].read(ProductValue(Map("mode" -> StringValue("write", Seq("mode")))))
  //    cfg shouldBe Cfg(Write).valid
  //  }
  //
  //  "Reader" should "collect all errors" in {
  //    val tree = ProductValue(
  //      Map(
  //        "a" -> StringValue("aaa", Seq("a")),
  //        "b" -> StringValue("test", Seq("b")),
  //        "c" -> ProductValue(
  //          Map(
  //            "d" -> StringValue("13.5", Seq("c", "d")),
  //            "e" -> StringValue("bbb", Seq("c", "e"))),
  //          Seq("c"))))
  //
  //    Reader[Config].read(tree) shouldBe Seq(
  //      "invalid StringValue 'aaa' at [a] -> For input string: \"aaa\"",
  //      "failed to covert value at [c.e] -> Failed to parse configuration at [0]: 'bbb'").invalid
  //  }
  //
  //  "Reader" should "handle invalid enums" in {
  //    val w = Reader[Mode].read(StringValue("test", Seq("a")))
  //    w shouldBe List("Invalid enum value 'test'. Expected one of [Read, Write]").invalid
  //  }
  //
  //  "Reader" should "provide all default fields" in {
  //    val w = Reader[WithDefault].read(MissingValue())
  //    w shouldBe WithDefault("test", Some(2)).valid
  //  }
  //
  //  "Reader" should "provide missing default values" in {
  //    val w = Reader[WithDefault].read(ProductValue(Map("a" -> StringValue("aaa", Seq("a")))))
  //    w shouldBe WithDefault("aaa", Some(2)).valid
  //  }
  //
  //  "Reader" should "handle nested all default values" in {
  //    val w = Reader[WithDefaultOuter].read(MissingValue())
  //    w shouldBe WithDefaultOuter(WithDefault("test", Some(2))).valid
  //  }
  //
  //  "Reader" should "handle nested missing default values" in {
  //    val w = Reader[WithDefaultOuter].read(
  //      ProductValue(Map("w" -> ProductValue(Map("a" -> StringValue("aaa", Seq("a")))))))
  //    w shouldBe WithDefaultOuter(WithDefault("aaa", Some(2))).valid
  //  }
}

object Samples {

  case class Config(a: Int, b: Option[String], c: Nested)

  case class Nested(d: Double, e: Deep)

  case class Deep(f: Map[String, String])

  case class Cfg(mode: Mode)

  sealed trait Mode

  case object Write extends Mode

  case object Read extends Mode

  case class WithDefault(a: String = "test", b: Option[Int] = Some(2))

  case class WithDefaultOuter(w: WithDefault = WithDefault())
}
