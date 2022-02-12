package com.prism.dataplatform.azure

import cats.effect.IO
import com.azure.ai.textanalytics.models.AnalyzeSentimentOptions
import com.azure.ai.textanalytics.{TextAnalyticsAsyncClient, TextAnalyticsClientBuilder}
import com.azure.core.credential.AzureKeyCredential
import com.prism.dataplatform.azure.AzureTextAnalytics.toIO
import reactor.core.publisher.Mono

import scala.collection.JavaConverters._

case class AzureTextAnalytics(key: String, endpoint: String) extends TextAnalytics[IO] {
  private lazy val client: TextAnalyticsAsyncClient = new TextAnalyticsClientBuilder()
    .credential(new AzureKeyCredential(key))
    .endpoint(endpoint)
    .buildAsyncClient()

  override def detectedLanguage(document: String): IO[String] = {
    val detectedLanguage = toIO(client.detectLanguage(document))
    detectedLanguage.map(_.getName)
  }

  override def analyzeSentiment(document: String): IO[String] = {
    toIO(client.analyzeSentiment(document)).map(_.getSentiment.toString)
  }

  override def analyzeSentimentWithOpinionMining(document: String, language: String): IO[String] = {
    val options = new AnalyzeSentimentOptions()
      .setIncludeStatistics(true)
      .setIncludeOpinionMining(true)
    toIO(client.analyzeSentiment(document, language, options)).map(s => s.getSentiment.toString)
  }

  override def extractKeyPhrases(document: String): IO[Seq[String]] = {
    toIO(client.extractKeyPhrases(document)).map(s => s.stream())
      .map(s => s.iterator().asScala.toSeq)
  }

  override def close(): Unit = println("Stopping client")
}

object AzureTextAnalytics {
  private def toIO[A](mono: Mono[A]): IO[A] =
    IO.fromCompletableFuture(IO(mono.toFuture))
}
