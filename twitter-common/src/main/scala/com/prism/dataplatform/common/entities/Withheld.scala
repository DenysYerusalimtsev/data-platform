package com.prism.dataplatform.common.entities

final case class Withheld(
                           copyright: Boolean,
                           country_codes: Seq[String],
                           scope: String
                         )
