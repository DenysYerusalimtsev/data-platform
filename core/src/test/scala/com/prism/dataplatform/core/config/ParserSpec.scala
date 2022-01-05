package com.prism.dataplatform.core.config

import cats.implicits._
import com.prism.dataplatform.core.BaseTest

import scala.collection.immutable.Seq

class ParserSpec extends BaseTest {
  "Parser" should "parse string values" in {
    Parser.parse("--a b --c d") shouldBe ProductValue(
      Map("a" -> StringValue("b", Seq("a")), "c" -> StringValue("d", Seq("c")))).valid
  }

  "Parser" should "parse list values" in {
    Parser.parse("--a b,c --d e,f") shouldBe ProductValue(
      Map(
        "a" -> StringValue("b,c", Seq("a")),
        "d" -> StringValue("e,f", Seq("d")))).valid
  }

  "Parser" should "parse empty value" in {
    Parser.parse("--a") shouldBe ProductValue(Map("a" -> MissingValue(Seq("a")))).valid
  }

  "Parser" should "parse multiple values" in {
    Parser.parse("--a b=1,c=2 --d --e 3") shouldBe ProductValue(
      Map(
        "a" -> StringValue("b=1,c=2", Seq("a")),
        "d" -> MissingValue(Seq("d")),
        "e" -> StringValue("3", Seq("e")))).valid
  }

  "Parser" should "produce configuration tree" in {
    val tree = Parser.parse(
      "--a b --nested.c 1 --nested.d true --nested.e.f 2 -- nested.e.g 3 --nested.e.h.j false")
    val expected = ProductValue(
      Map(
        "a" -> StringValue("b", List("a")),
        "nested" -> ProductValue(
          Map(
            "e" -> ProductValue(
              Map(
                "h" -> ProductValue(
                  Map("j" -> StringValue("false", List("nested", "e", "h", "j"))),
                  List("nested", "e", "h")),
                "g" -> StringValue("3", List("nested", "e", "g")),
                "f" -> StringValue("2", List("nested", "e", "f"))),
              List("nested", "e")),
            "d" -> StringValue("true", List("nested", "d")),
            "c" -> StringValue("1", List("nested", "c"))),
          List("nested"))),
      List())
    tree should be(expected.valid)
  }

  "Parser" should "parse empty config" in {
    Parser.parse("") shouldBe ProductValue().valid
  }


  "Parser" should "fail on invalid key" in {
    Parser.parse("-a") shouldBe Seq("Failed to parse configuration at [0]: '-a'").invalid
  }

  "Parser" should "fail on ambiguous tree" in {
    // --a.b implies that b is a string, --a.b.d implies that b is product with d field
    Parser.parse("--a.b c --a.b.d e") shouldBe Seq("ambiguous values at [a.b]").invalid
  }
}
