package com.prism.dataplatform.common.entities.responses

import com.prism.dataplatform.common.entities.{Rule, RuleMeta}

case class RulesResponse(
                          data: Option[Seq[Rule]],
                          meta: Option[RuleMeta]
                        )