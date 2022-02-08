package com.prism.dataplatform.twitter

import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.flink.syntax.toDataStreamOps
import com.prism.dataplatform.twitter.analyzer.AnalyzeSentiment
import com.prism.dataplatform.twitter.config.{Config, TwitterConfig}
import com.prism.dataplatform.twitter.entities.responses.TweetResponse
import com.prism.dataplatform.twitterconnector.Twitter
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.DataStream

final class AnalyzeTweets extends FlinkJob[Config] {
  override def script(): Unit = {
    logger.info("Application started")
    val tweets: DataStream[TweetResponse] = env.addSource(Twitter(buildTwitterConfig(config)))
      .name("Tweets")

//    tweets.via(AnalyzeSentiment(config.azure))

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