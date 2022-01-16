package com.prism.dataplatform.azure

import scala.language.higherKinds

trait TextAnalytics[F[_]] extends AutoCloseable {
  def detectedLanguage(document: String): F[String]

  def analyzeSentiment(document: String): F[String]

  def analyzeSentimentWithOpinionMining(document: String, language: String): F[String]

  def extractKeyPhrases(document: String): F[Seq[String]]
}
