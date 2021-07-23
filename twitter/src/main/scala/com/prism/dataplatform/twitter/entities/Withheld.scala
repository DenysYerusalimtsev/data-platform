package com.prism.dataplatform.twitter.entities

import com.prism.dataplatform.twitter.entities.enums.WithheldScope.WithheldScope

final case class Withheld(
                           copyright: Boolean,
                           country_codes: Seq[String],
                           scope: WithheldScope
                         )
