package com.prism.dataplatform.common.entities.responses

import com.prism.dataplatform.common.entities.{Rule, RuleMeta}

case class StreamRulesResponse(
                                data: Option[Seq[Rule]],
                                meta: RuleMeta
                              )
