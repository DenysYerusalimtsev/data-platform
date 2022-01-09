package com.prism.dataplatform.twitter.analyzer

import cats.effect.IO
import com.prism.dataplatform.azure.TextAnalytics
import com.prism.dataplatform.flink.AsyncFunction

object AnalyzeSentiment {
  def apply(client: () => TextAnalytics[IO]): Unit = {
    AsyncFunction.setup(ctx => {
      val textAnalyticsClient = client()

    })
  }
}
