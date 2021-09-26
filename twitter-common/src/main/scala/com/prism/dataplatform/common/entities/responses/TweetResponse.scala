package com.prism.dataplatform.common.entities.responses

import com.prism.dataplatform.common.entities.{Tweet, TweetIncludes}

final case class TweetResponse(data: Option[Tweet],
                               includes: Option[TweetIncludes],
                               errors: Seq[Error])
