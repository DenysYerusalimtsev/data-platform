package com.prism.dataplatform.twitterconnector

import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.twitter.client.TwitterRestClient
import com.prism.dataplatform.twitter.config.{TConfig, TwitterConfig}
import com.prism.dataplatform.twitter.entities.responses.TweetsResponse
import com.prism.dataplatform.twitter.serializer.Serializer
import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}

case class Twitter(config: TConfig) extends RichSourceFunction[TweetsResponse]
  with Serializer
  with LazyLogging {
  @transient var twitterClient: TwitterRestClient = _
  @transient var running: Boolean = _

  override def open(parameters: Configuration): Unit = {
    twitterClient = TwitterRestClient(config)
    running = true
  }

  override def run(ctx: SourceFunction.SourceContext[TweetsResponse]): Unit = {
    logger.info("Connecting to Twitter...")
    val program = for {
      token <- twitterClient.authenticate
      json <- twitterClient.filteredStringStream(token.access_token)
      tweets <- fromJson[TweetsResponse](json)
    } yield tweets

    program.unsafeRunSync()
  }

  override def cancel(): Unit = {
    logger.info("Closing connection to Twitter...")
    running = false
  }
}
