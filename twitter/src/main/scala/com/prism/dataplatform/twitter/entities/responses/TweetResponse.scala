package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{Tweet, TweetIncludes}

final case class TweetResponse(data: Option[Tweet],
                               includes: Option[TweetIncludes],
                               errors: Array[Error])
