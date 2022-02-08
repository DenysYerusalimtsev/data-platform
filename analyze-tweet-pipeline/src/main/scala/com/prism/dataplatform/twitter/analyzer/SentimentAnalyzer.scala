package com.prism.dataplatform.twitter.analyzer

import com.prism.dataplatform.azure.TextAnalytics
import com.prism.dataplatform.twitter.entities.responses.TweetResponse

import scala.language.higherKinds

case class SentimentAnalyzer[F[_]](client: TextAnalytics[F]) extends AutoCloseable {
  def analyzeSentiment(tweet: TweetResponse): F[String] = {
    val document = tweet.data.flatMap(_.text).getOrElse("")
    val language = tweet.data.flatMap(_.lang).getOrElse("")

    client.analyzeSentimentWithOpinionMining(
      document, language)
  }

  def extractKeyPhrases(tweet: TweetResponse): F[Seq[String]] = {
    val document = tweet.data.flatMap(_.text).getOrElse("")

    client.extractKeyPhrases(document)
  }

  override def close(): Unit = client.close()
}
