package com.prism.dataplatform.twitter

import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.client.TwitterClient
import com.prism.dataplatform.twitter.config.Config

final class AnalyzeTweets extends FlinkJob[Config]
  with TwitterClient {
  override def script(): Unit = {
    logger.info("Application started")
    setupSettings(config.twitter)
  }
}
