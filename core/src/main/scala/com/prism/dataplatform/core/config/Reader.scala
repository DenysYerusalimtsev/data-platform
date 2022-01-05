package com.prism.dataplatform.core.config

import cats.data.{NonEmptyList, Validated}
import cats.implicits._
import com.prism.dataplatform.core.time.{richDuration, Duration, Instant, LocalDate}
import magnolia1.{CaseClass, Magnolia, SealedTrait}

import scala.collection.immutable.Seq
import scala.concurrent.duration.{FiniteDuration, Duration => SDuration}
import scala.language.experimental.macros
import scala.util.Try

trait Reader[T] {
  def read(config: ConfigNode): Validated[List[String], T]
}

object Reader extends LowPriorityImplicits {

  def apply[T](implicit reader: Reader[T]): Reader[T] = reader

  def parse[T](fn: String => T): Reader[T] = {
    case cfg: StringValue =>
      Validated.fromTry(Try(fn(cfg.value))).leftMap(e => failed(cfg, Some(e.getMessage)))
    case cfg => failed(cfg).invalid
  }

  def parseWith[T](fn: String => Validated[NonEmptyList[String], T]): Reader[T] = {
    case cfg: StringValue => fn(cfg.value).leftMap(_.toList)
    case cfg              => failed(cfg).invalid
  }

  implicit val stringReader: Reader[String] = parse(identity)
  implicit val intReader: Reader[Int] = parse(_.toInt)
  implicit val longReader: Reader[Long] = parse(_.toLong)
  implicit val doubleReader: Reader[Double] = parse(_.toDouble)
  implicit val instantReader: Reader[Instant] = parse(Instant(_))
  implicit val durationReader: Reader[Duration] = parse(Duration.parse)
  implicit val dateReader: Reader[LocalDate] = parse(LocalDate(_))
  implicit val sDurationReader: Reader[SDuration] = parse(SDuration(_))
  implicit val booleanReader: Reader[Boolean] = parse(_.toBoolean)
  implicit val finiteDurationReader: Reader[FiniteDuration] =
    parse(Duration.parse(_).toFiniteDuration)

  implicit def optionReader[T: Reader]: Reader[Option[T]] = {
    case MissingValue(_) => None.valid
    case cfg             => Reader[T].read(cfg).map(Some(_))
  }

  implicit def seqReader[T: Reader]: Reader[Seq[T]] = {
    case str: StringValue =>
      str.toSeq.andThen(seq => {
        seq.zipWithIndex
          .map { case (el, i) => Reader[T].read(StringValue(el, str.path :+ s"[$i]")) }
          .toList
          .traverse(identity)
      })
    case MissingValue(_) => Nil.valid
    case cfg             => failed(cfg, Some("config is not a list")).invalid
  }

  implicit def mapReader[T: Reader]: Reader[Map[String, T]] = {
    case pv: ProductValue => readMap[T](pv)
    case str: StringValue => str.toProduct.andThen(readMap[T])
    case MissingValue(_)  => Map.empty[String, T].valid
    case cfg              => failed(cfg, Some("config is not a map")).invalid
  }

  private def readMap[T: Reader](cfg: ProductValue): Validated[List[String], Map[String, T]] = {
    val result = cfg.value.map {
      case (key, el) => Reader[T].read(el).map(key -> _)
    }
    result.toList.sequence.map(_.toMap)
  }

  def failed(cfg: ConfigNode, reason: Option[String] = None): List[String] = {
    List(s"invalid ${cfg.show}${reason.map(" -> " + _).getOrElse("")}")
  }
}

// Magnolia magic
trait LowPriorityImplicits {

  type Typeclass[T] = Reader[T]

  def combine[T](ctx: CaseClass[Reader, T]): Reader[T] = (config: ConfigNode) => {
    ctx.parameters.toList
      .traverse(p => {
        val cfg = config match {
          case pv: ProductValue => pv.get(p.label)
          case _                => config
        }
        cfg match {
          case cfg: MissingValue => p.default.map(_.valid).getOrElse(p.typeclass.read(cfg))
          case cfg               => p.typeclass.read(cfg)
        }
      })
      .map(ctx.rawConstruct)
  }

  def dispatch[T](ctx: SealedTrait[Reader, T]): Reader[T] = {
    case config @ StringValue(string, _) =>
      val subtypes = ctx.subtypes.map(_.typeName.short).sorted.mkString("[", ", ", "]")
      ctx.subtypes
        .collectFirst {
          case tpe if tpe.typeName.short.equalsIgnoreCase(string) =>
            tpe.typeclass.read(config)
        }
        .getOrElse(List(s"Invalid enum value '$string'. Expected one of $subtypes").invalid)
    case cfg => Reader.failed(cfg).invalid
  }

  implicit def gen[T]: Reader[T] = macro Magnolia.gen[T]
}
