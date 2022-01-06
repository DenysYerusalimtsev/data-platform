package com.prism.dataplatform.twitter

import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.config.{Config, TwitterConfig}
import com.prism.dataplatform.twitterconnector.Twitter
import org.apache.flink.api.scala.createTypeInformation

final class AnalyzeTweets extends FlinkJob[Config] {
  override def script(): Unit = {
    logger.info("Application started")
    val tweets = env.addSource(Twitter(buildTwitterConfig(config)))
      .name("Tweets")

    tweets.print()
  }

  private def buildTwitterConfig(config: Config): TwitterConfig = {
    TwitterConfig(
      config.twitter.consumerKey,
      config.twitter.consumerSecret,
      config.twitter.bearerToken,
      config.twitter.token,
      config.twitter.tokenSecret
    )
  }
}

//TO DO: cache
// Source -> flatMap monads