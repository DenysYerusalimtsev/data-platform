package com.prism.dataplatform.core.config

import cats.data.Validated
import cats.implicits._

import scala.collection.immutable.Seq
import scala.language.higherKinds
import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers

/** Main entry point to parse configuration parameters (i.e. job command line input)
 * as configuration case classes.
 *
 * Parser supports following formats for input parameters:
 *  - string values: `--key1 value1 --key2 value2`
 *  - list values: `--key value1,value2`
 *  - map values: `--key=k1=v1,k2=v2`
 *  - empty values: `--key` (can be converted to empty options, lists, etc.)
 *  - grouped values: `--cfg.a value1 --cfg.b value2` (where `a` and `b` will be grouped under `cfg`)
 *
 * Parser provides functionality to convert parsed configuration directly into
 * specified (hierarchical) types (if there is implicit {{Read}} for the given type).
 *
 * Conversion for most common types (i.e. case classes, collections, primitives)
 * is provided out of the box. For example:
 *
 * {{{
 *   case class Config(a: Int, b: Option[String], c: Nested)
 *   case class Nested(d: Boolean, e: Option[String], f: Int = 12)
 *
 *   // will produce Config(1, None, Nested(true, Some("test"), 12)
 *   Parser.read[Config]("--a 1 --c.d true --c.e test")
 * }}}
 *
 * It is also possible to provide custom Readers:
 *
 * {{{
 *   sealed trait Mode
 *   case object Write
 *   case object Read
 *   case class Config(mode: Mode)
 *
 *   implicit modeReader: Reader[Mode] = Reader.parse {
 *     case "write" => Write
 *     case "read"  => Read
 *   }
 *   // will produce Config(Read)
 *   Parser.read[Config]("--mode read")
 * }}}
 */
object Parser {

  type Parsed[T] = Validated[List[String], T]

  def read[T: Reader](configs: Seq[String]): Parsed[T] = {
    read(configs.mkString(" "))
  }

  def read[T: Reader](config: String): Parsed[T] = {
    parse(config).andThen(Reader[T].read)
  }

  def parse(config: String): Parsed[ConfigNode] = {
    parse(config, ConfigParser.configs).andThen(collapseTree(_))
  }

  private[config] def parse[T](input: String, parser: ConfigParser.Parser[T]): Parsed[T] = {
    ConfigParser.parse(parser, input) match {
      case ConfigParser.Success(matched, in) if in.atEnd => matched.valid
      case ConfigParser.Success(_, in) =>
        val remainder = in.source.subSequence(in.offset, in.source.length())
        List(s"Failed to parse configuration at [${in.offset}]: '$remainder'").invalid
      case ConfigParser.NoSuccess(msg, _) =>
        List(s"Failed to parse configuration: $msg").invalid
    }
  }

  private def collapseTree(configs: Seq[ConfigNode], depth: Int = 0): Parsed[ConfigNode] = {
    val path = configs.headOption.map(_.path.take(depth)).getOrElse(Nil)
    val value = configs.groupBy(_.path.apply(depth)).map {
      case (key, Seq(c)) if c.path.size == depth + 1 => (key -> c).valid
      case (key, cs) if cs.exists(_.path.size == depth + 1) =>
        List(s"ambiguous values at [${(path :+ key).mkString(".")}]").invalid
      case (key, cs) => collapseTree(cs, depth + 1).map(key -> _)
    }
    value.toList.traverse(identity).map(vs => ProductValue(vs.toMap, path))
  }

  private[config] object ConfigParser extends RegexParsers {

    type Path = Seq[String]

    // config path, i.e. `--a.b.c `
    def name: Parser[String] = """[\w_-]+""".r
    def path: Parser[Path] = "--" ~> rep1sep(name, ".")

    // string config, i.e. `--field a`
    def stringValue: Parser[String] = not(" --", "--")
    def stringConfig: Parser[Path => ConfigNode] = stringValue.map {
      case s if s.isEmpty => MissingValue
      case s              => StringValue(s, _)
    }

    def config: Parser[ConfigNode] = (path ~ stringConfig) map {
      case path ~ factory => factory(path)
    }
    def configs: Parser[Seq[ConfigNode]] = config.*

    def not(sequences: String*): Parser[String] = {
      // negative lookahead pattern to *not* match *multi-character* sequences
      new Regex(s"(?:(?!${sequences.map(s => s"($s)").mkString("|")}).)*")
    }

    // parse string value as a list, i.e. `--field a,b,c`
    def seqValue: Parser[String] = "[^,]+".r
    def seqConfig: Parser[Seq[String]] = rep1sep(seqValue, ",")

    // parse string value as a map, i.e. `--field a=1,b=2,c=3`
    def mapKey: Parser[String] = "[^=]+".r
    def mapVal: Parser[String] = "[^,]+".r
    def mapPair: Parser[(String, String)] = ((mapKey <~ "=") ~ mapVal) map { case k ~ v => k -> v }
    def mapConfig: Parser[Map[String, String]] = repsep(mapPair, ",") map { _.toMap }
  }
}
