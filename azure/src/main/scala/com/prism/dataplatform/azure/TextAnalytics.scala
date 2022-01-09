package com.prism.dataplatform.azure

trait TextAnalytics[F[_]] {
  def detectedLanguage(document: String): F[String]

  def analyzeSentiment(document: String): Unit

  def analyzeSentimentWithOpinionMining(document: String, language: String): F[String]

  def extractKeyPhrases(document: String): Unit
}
