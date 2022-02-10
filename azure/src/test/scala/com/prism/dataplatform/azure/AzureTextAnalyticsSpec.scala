package com.prism.dataplatform.azure

import cats.effect.unsafe.implicits.global

class AzureTextAnalyticsSpec extends BaseTest {
  behavior of classOf[AzureTextAnalytics].getSimpleName

  val client = new AzureTextAnalytics(
    key = "084c7369342949bdb3f3935e5bc1950f",
    endpoint = "https://prismanalyticscs.cognitiveservices.azure.com/"
  )

  it should "successfully analyze sentiment" in {
    val document = "Life is good"

    val testCase = for {
      sentiment <- client.analyzeSentiment(document)
    } yield sentiment

    testCase.map(resp => {
      assert(resp.nonEmpty)
      assert(resp.toLowerCase().contains("positive"))
    }).unsafeRunSync()
  }

  it should "successfully extract key phrases" in {
    val document = "Life is good"

    val testCase = for {
      keyPhrases <- client.extractKeyPhrases(document)
    } yield keyPhrases

    testCase.map(resp => {
      assert(resp.nonEmpty)
      assert(resp.contains("Life"))
    }).unsafeRunSync()
  }

  it should "successfully detect language" in {
    val document = "Life is good"

    val testCase = for {
      language <- client.detectedLanguage(document)
    } yield language

    testCase.map(resp => {
      assert(resp.nonEmpty)
      assert(resp.contains("English"))
    }).unsafeRunSync()
  }
}
