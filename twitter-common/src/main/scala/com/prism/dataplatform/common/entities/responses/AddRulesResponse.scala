package com.prism.dataplatform.common.entities.responses

import com.prism.dataplatform.common.entities.{Error, RuleMeta}

case class AddRulesResponse(
                             meta: Option[RuleMeta],
                             errors: Option[Seq[Error]]
                           )
