package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{Error, RuleMeta}

case class DeleteRulesResponse(
                                meta: Option[RuleMeta],
                                errors: Option[Seq[Error]]
                              )
