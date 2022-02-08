package com.prism.dataplatform.twitter.entities

case class SentimentTweet(
                           data: Tweet,
                           includes: Option[TweetIncludes],
                           sentiment: String,
                           keyWords: Seq[String]
                         )
