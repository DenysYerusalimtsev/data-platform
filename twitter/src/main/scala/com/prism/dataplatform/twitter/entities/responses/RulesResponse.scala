package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{Rule, RuleMeta}

case class RulesResponse(
                          data: Option[Array[Rule]],
                          meta: Option[RuleMeta]
                        )