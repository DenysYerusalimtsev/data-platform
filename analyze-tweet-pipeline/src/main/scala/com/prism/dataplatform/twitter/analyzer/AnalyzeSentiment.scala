package com.prism.dataplatform.twitter.analyzer

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.azure.TextAnalytics
import com.prism.dataplatform.flink.AsyncFunction
import com.prism.dataplatform.twitter.AnalyzedTweets
import com.prism.dataplatform.twitter.entities.SentimentTweet
import org.apache.flink.streaming.api.scala._

object AnalyzeSentiment {
  def apply(client: TextAnalytics[IO], threads: Int): AnalyzedTweets = {
    AsyncFunction.setup(_ => {
      SentimentAnalyzer[IO](client)
    })
      .parallelism(threads)
      .stop(_.close())
      .flatMap {
        (analyzer, tweet) =>
          val enrichment = for {
            sentiment <- analyzer.analyzeSentiment(tweet)
            keyWords <- analyzer.extractKeyPhrases(tweet)
            sentimentTweet <- IO.pure(SentimentTweet(
              data = tweet.data.getOrElse(throw new Exception("Data is required field for tweet!")),
              includes = tweet.includes,
              sentiment = sentiment,
              keyWords = keyWords))
          } yield sentimentTweet

          enrichment.map(Seq(_)).unsafeToFuture()
      }
  }
}
