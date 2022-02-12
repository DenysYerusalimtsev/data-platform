package com.prism.dataplatform

import com.prism.dataplatform.flink.DataFlow
import com.prism.dataplatform.twitter.entities.SentimentTweet
import com.prism.dataplatform.twitter.entities.responses.TweetResponse

package object twitter {
  type AnalyzedTweets = DataFlow[TweetResponse, SentimentTweet]
//  type AnalyzedTweets = DataFlow[TweetResponse, Int]
}
