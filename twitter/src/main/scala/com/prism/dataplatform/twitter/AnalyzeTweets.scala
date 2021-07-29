package com.prism.dataplatform.twitter

import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.client.TwitterClient
import com.prism.dataplatform.twitter.config.Config

final class AnalyzeTweets extends FlinkJob[Config]
  with TwitterClient {
  override def script(): Unit = {
    logger.info("Application started")
    setupSettings(config.twitter)
    val tweets = authenticate().flatMap(token => searchTweets("lakers", token.access_token)).unsafeRunSync()
    print(tweets)
  }
}

//TO DO: cache
// Source -> flatMap monads