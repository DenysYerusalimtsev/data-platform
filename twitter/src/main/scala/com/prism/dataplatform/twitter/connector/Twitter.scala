package com.prism.dataplatform.twitter.connector

import com.prism.dataplatform.twitter.config.{TwitterConfig, TwitterProperties}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.twitter.TwitterSource

trait Twitter extends TwitterProperties {
  def setupTwitterConnection(config: TwitterConfig)(implicit env: StreamExecutionEnvironment): DataStream[String] = {
    val twitterProperties = setupProperties(config)
    env.addSource(new TwitterSource(twitterProperties))
  }
}
