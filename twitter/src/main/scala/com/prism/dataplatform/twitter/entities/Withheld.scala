package com.prism.dataplatform.twitter.entities

final case class Withheld(
                           copyright: Option[Boolean],
                           country_codes: Array[String],
                           scope: Option[String]
                         )
