package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{Error, Meta, Tweet, TweetIncludes}

final case class TweetsResponse(data: Array[Tweet],
                                includes: Option[TweetIncludes],
                                errors: Option[Array[Error]],
                                meta: Option[Meta])
