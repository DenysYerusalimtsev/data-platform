package com.prism.dataplatform.azure

import cats.effect.IO
import com.azure.ai.textanalytics.{TextAnalyticsAsyncClient, TextAnalyticsClientBuilder}
import com.azure.ai.textanalytics.models.AnalyzeSentimentOptions
import com.azure.core.credential.AzureKeyCredential
import com.prism.dataplatform.azure.AzureTextAnalytics.toIO
import reactor.core.publisher.Mono

class AzureTextAnalytics(key: String, endpoint: String) extends TextAnalytics[IO] {
  val client: TextAnalyticsAsyncClient = new TextAnalyticsClientBuilder()
    .credential(new AzureKeyCredential(key))
    .endpoint(endpoint)
    .buildAsyncClient();

  override def detectedLanguage(document: String): IO[String] = {
    val detectedLanguage = toIO(client.detectLanguage(document))
    detectedLanguage.map(_.getName)
  }

  override def analyzeSentiment(document: String): Unit = {
    toIO(client.analyzeSentiment(document))
  }

  override def analyzeSentimentWithOpinionMining(document: String, language: String): IO[String] = {
    val options = new AnalyzeSentimentOptions()
      .setIncludeStatistics(true)
      .setIncludeOpinionMining(true)
    toIO(client.analyzeSentiment(document, language, options))
    IO("")
  }

  override def extractKeyPhrases(document: String): Unit = {
    toIO(client.extractKeyPhrases(document))
  }
}

object AzureTextAnalytics {
  private def toIO[A](mono: Mono[A]): IO[A] =
    IO.fromCompletableFuture(IO(mono.toFuture))
}
