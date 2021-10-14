package com.prism.dataplatform.twitter.entities

final case class Withheld(
                           copyright: Boolean,
                           country_codes: Array[String],
                           scope: String
                         )
