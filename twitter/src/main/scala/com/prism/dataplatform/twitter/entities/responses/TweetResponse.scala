package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{Error, MatchingRules, Tweet, TweetIncludes, TweetTest}

final case class TweetResponse(data: Option[Tweet],
                               includes: Option[TweetIncludes],
                               errors: Array[Error],
                               matching_rules: MatchingRules)

final case class TestTweet(data: Option[TweetTest])
