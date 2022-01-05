package com.prism.dataplatform.core.config

import scala.collection.immutable.Seq

sealed trait ConfigNode {

  def path: Seq[String]
  def show: String
}

sealed trait ConfigValue[T] extends ConfigNode {

  def value: T
  def show: String = s"${getClass.getSimpleName} '$value' at $pathLabel"
  def pathLabel: String = path.mkString("[", ".", "]")
}

/** simple string value, i.e. `--field a` or `--nested.field a` */
case class StringValue(value: String, path: Seq[String] = Nil) extends ConfigValue[String] {

  /** parse string value as a list, i.e. --field a,b,c */
  def toSeq: Parser.Parsed[Seq[String]] =
    Parser.parse(value, Parser.ConfigParser.seqConfig).leftMap(appendErrPath)

  /** parse string value as a map, i.e. --field a=1,b=2,c=3 */
  def toMap: Parser.Parsed[Map[String, String]] =
    Parser.parse(value, Parser.ConfigParser.mapConfig).leftMap(appendErrPath)

  /** parse string to a map and convert map values into nested string configs */
  def toProduct: Parser.Parsed[ProductValue] =
    toMap.map(m => ProductValue(m.map { case (k, v) => k -> StringValue(v, path :+ k) }, path))

  private def appendErrPath(errs: List[String]): List[String] =
    List(s"failed to covert value at $pathLabel -> ${errs.mkString(",")}")
}

/** group of values, i.e. `--nested.a 1 --nested.b true` */
case class ProductValue(value: Map[String, ConfigNode] = Map.empty, path: Seq[String] = Nil)
  extends ConfigValue[Map[String, ConfigNode]] {

  def get(name: String): ConfigNode = value.getOrElse(name, MissingValue(path :+ name))
}

/** indicates that optional value is missing (or failed required value) */
case class MissingValue(path: Seq[String] = Nil) extends ConfigNode {
  override def show: String = s"missing value at ${path.mkString("[", ".", "]")}"
}
