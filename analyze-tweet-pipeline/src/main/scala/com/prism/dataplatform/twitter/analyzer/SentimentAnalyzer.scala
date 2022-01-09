package com.prism.dataplatform.twitter.analyzer

import cats.effect.IO
import com.prism.dataplatform.azure.TextAnalytics
import com.prism.dataplatform.twitter.entities.responses.TweetResponse

case class SentimentAnalyzer(client: TextAnalytics[IO]) {
  def analyzeSentiment(tweet: TweetResponse): Unit = {


    client.analyzeSentimentWithOpinionMining(
      document = tweet.data.flatMap(_.text).getOrElse(""),
      language = "en")


  }
}
