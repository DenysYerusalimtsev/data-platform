package com.prism.dataplatform.common.entities.responses

import com.prism.dataplatform.common.entities.{Error, RuleMeta}

case class DeleteRulesResponse(
                                meta: Option[RuleMeta],
                                errors: Option[Seq[Error]]
                              )
