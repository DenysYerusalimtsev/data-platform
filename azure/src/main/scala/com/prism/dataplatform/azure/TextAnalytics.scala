package com.prism.dataplatform.azure

trait TextAnalytics {
  def analyzeSentiment(document: String): Unit

  def analyzeSentimentWithOpinionMining(document: String, language: String): Unit

  def extractKeyPhrases(document: String): Unit
}
