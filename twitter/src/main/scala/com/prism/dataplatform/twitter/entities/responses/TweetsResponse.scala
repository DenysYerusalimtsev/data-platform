package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{Meta, Tweet, TweetIncludes}

final case class TweetsResponse(data: Seq[Tweet],
                                includes: Option[TweetIncludes],
                                errors: Seq[Error],
                                meta: Seq[Meta])
