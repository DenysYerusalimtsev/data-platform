package com.prism.dataplatform.twitterconnector

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO}
import com.prism.dataplatform.twitter.client.{TwitterRestClient, TwitterStreamingClient}
import com.prism.dataplatform.twitter.config.TConfig
import com.prism.dataplatform.twitter.entities.responses.TweetsResponse
import com.prism.dataplatform.twitter.serializer.JsonSerializer
import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.RichSourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import fs2.Stream

case class Twitter(config: TConfig) extends RichSourceFunction[TweetsResponse]
  with LazyLogging {
  @transient var serializer: JsonSerializer = _
  @transient var twitterClient: TwitterRestClient = _
  @transient var streamingClient: TwitterStreamingClient[IO] = _
  @transient var running: Boolean = _

  override def open(parameters: Configuration): Unit = {
    serializer = new JsonSerializer()
    twitterClient = TwitterRestClient(config)
    streamingClient = new TwitterStreamingClient[IO](config)
    running = true
  }

  override def run(ctx: SourceContext[TweetsResponse]): Unit = {
    logger.info("Connecting to Twitter...")
    while (running) {
      val token = twitterClient.authenticate.unsafeRunSync()
      val tweets = streamingClient.streamTweets(token.access_token)
      tweets.map(tweet => process(tweet)(ctx)).compile.drain.unsafeRunSync
    }
  }

  override def cancel(): Unit = {
    logger.info("Closing connection to Twitter...")
    running = false
  }

  private def process(response: TweetsResponse)(implicit ctx: SourceContext[TweetsResponse]): IO[Unit] = {
    ctx.synchronized {
      IO(ctx.collect(response))
    }
  }
}