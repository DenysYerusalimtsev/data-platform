package com.prism.dataplatform.twitter.analyzer

import cats.effect.IO
import com.prism.dataplatform.azure.TextAnalytics
import com.prism.dataplatform.flink.AsyncFunction
import com.prism.dataplatform.twitter.AnalyzedTweets
import org.apache.flink.streaming.api.scala._

object AnalyzeSentiment {
  def apply(client: () => TextAnalytics[IO], threads: Int): AnalyzedTweets = {
    AsyncFunction.setup(ctx => {
      val textAnalyticsClient = client()
      SentimentAnalyzer[IO](textAnalyticsClient)
    })
      .parallelism(threads)
      .stop(analyzer => analyzer.close())
      .flatMap {
        (analyzer, tweets) =>
          analyzer.analyzeSentiment(tweets)

      }
  }
}
