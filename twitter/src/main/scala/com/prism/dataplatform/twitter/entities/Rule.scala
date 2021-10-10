package com.prism.dataplatform.twitter.entities

case class Rule(
                 value: String,
                 tag: Option[String]
               ) extends Identifiable {
  val id: String = s"prism_$value"
}
