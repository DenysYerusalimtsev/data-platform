package com.prism.dataplatform.twitter

import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.config.Config
import com.prism.dataplatform.twitter.connector.Twitter

final class AnalyzeTweets extends FlinkJob[Config]
  with Twitter {
  override def script(): Unit = {
    logger.info("Application started")
  }
}
