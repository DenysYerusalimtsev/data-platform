package com.prism.dataplatform.twitterconnector

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.twitter.client.TwitterRestClient
import com.prism.dataplatform.twitter.config.TConfig
import com.prism.dataplatform.twitter.entities.responses.TweetsResponse
import com.prism.dataplatform.twitter.serializer.JsonSerializer
import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}

case class Twitter(config: TConfig) extends RichSourceFunction[TweetsResponse]
  with LazyLogging {
  @transient var serializer: JsonSerializer = _
  @transient var twitterClient: TwitterRestClient = _
  @transient var running: Boolean = _

  override def open(parameters: Configuration): Unit = {
    serializer = new JsonSerializer()
    twitterClient = TwitterRestClient(config)
    running = true
  }

  override def run(ctx: SourceFunction.SourceContext[TweetsResponse]): Unit = {
    logger.info("Connecting to Twitter...")
    val program = for {
      token <- twitterClient.authenticate
      json <- twitterClient.filteredStringStream(token.access_token)
      tweets <- serializer.fromJson[TweetsResponse](json)
      _ <- process(tweets)(ctx)
    } yield ()
    program.unsafeRunSync()
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