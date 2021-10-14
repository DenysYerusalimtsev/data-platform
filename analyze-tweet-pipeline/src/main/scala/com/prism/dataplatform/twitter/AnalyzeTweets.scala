package com.prism.dataplatform.twitter

import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.config.{Config, TConfig}
import com.prism.dataplatform.twitterconnector.Twitter
import org.apache.flink.api.scala.createTypeInformation

final class AnalyzeTweets extends FlinkJob[Config] {
  override def script(): Unit = {
    logger.info("Application started")

    val tconfig = TConfig(
      config.twitter.consumerKey,
      config.twitter.consumerSecret,
      config.twitter.bearerToken,
      config.twitter.token,
      config.twitter.tokenSecret
    )
    val tweets =
      env.addSource(Twitter(tconfig))
        .name("Tweets")
        .print()
  }
}

//TO DO: cache
// Source -> flatMap monads