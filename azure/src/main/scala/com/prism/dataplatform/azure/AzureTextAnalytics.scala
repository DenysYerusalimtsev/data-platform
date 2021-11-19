package com.prism.dataplatform.azure

import com.azure.ai.textanalytics.{TextAnalyticsAsyncClient, TextAnalyticsClientBuilder}
import com.azure.core.credential.AzureKeyCredential

class AzureTextAnalytics() extends TextAnalytics {
  val client: TextAnalyticsAsyncClient = new TextAnalyticsClientBuilder()
    .credential(new AzureKeyCredential("{key}"))
    .endpoint("{endpoint}")
    .buildAsyncClient();

  override def analyzeSentiment(document: String): Unit = {

  }

  override def analyzeSentimentWithOpinionMining(document: String): Unit = {

  }

  override def extractKeyPhrases(document: String): Unit = {

  }
}
