package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{Meta, Tweet, TweetIncludes, Error}

final case class TweetsResponse(data: Array[Tweet],
                                includes: Option[TweetIncludes],
                                errors: Option[Array[Error]],
                                meta: Option[Meta])
