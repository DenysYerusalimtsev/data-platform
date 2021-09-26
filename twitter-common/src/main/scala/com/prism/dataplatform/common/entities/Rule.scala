package com.prism.dataplatform.common.entities

case class Rule(
                 value: String,
                 tag: Option[String]
               ) extends Identifiable {
  val id: String = s"prism_$value"
}
