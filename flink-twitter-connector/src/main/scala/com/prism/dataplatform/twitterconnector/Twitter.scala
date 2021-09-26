package com.prism.dataplatform.twitterconnector

import com.prism.dataplatform.common.entities.responses.TweetResponse
import com.prism.dataplatform.twitterconnector.config.TwitterConfig
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}

case class Twitter(config: TwitterConfig) extends RichSourceFunction[TweetResponse] {

  override def open(parameters: Configuration): Unit = {

  }

  override def run(ctx: SourceFunction.SourceContext[TweetResponse]): Unit = {

  }

  override def cancel(): Unit = {

  }
}
