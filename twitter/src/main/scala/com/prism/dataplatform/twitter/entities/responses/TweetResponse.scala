package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{Error, MatchingRules, Tweet, TweetIncludes}

final case class TweetResponse(data: Option[Tweet],
                               includes: Option[TweetIncludes],
                               errors: Array[Error],
                               matching_rules: MatchingRules)
