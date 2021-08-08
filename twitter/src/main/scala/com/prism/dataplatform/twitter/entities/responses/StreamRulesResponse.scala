package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{Rule, RuleMeta}

case class StreamRulesResponse(
                                data: Option[Seq[Rule]],
                                meta: RuleMeta
                              )
