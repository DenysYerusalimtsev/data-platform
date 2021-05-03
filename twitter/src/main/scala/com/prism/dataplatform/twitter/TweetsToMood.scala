package com.prism.dataplatform.twitter

import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.config.{Config, TwitterSourceProperties}
import org.apache.flink.streaming.connectors.twitter.TwitterSource

class TweetsToMood extends FlinkJob[Config]
  with TwitterSourceProperties {
  override def script(): Unit = {
    logger.info("Application started")
    val twitterSourceProperties = setupProperties(config.twitter)

    env.addSource(new TwitterSource(twitterSourceProperties))
  }
}
