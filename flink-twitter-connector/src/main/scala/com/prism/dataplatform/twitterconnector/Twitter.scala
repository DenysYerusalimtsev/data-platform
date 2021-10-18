package com.prism.dataplatform.twitterconnector

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.twitter.client.{TwitterRestClient, TwitterStreamingClient}
import com.prism.dataplatform.twitter.config.TConfig
import com.prism.dataplatform.twitter.entities.responses.TweetResponse
import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.RichSourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.json4s._
import org.json4s.native.JsonMethods._


case class Twitter(config: TConfig) extends RichSourceFunction[TweetResponse]
  with LazyLogging {
  @transient var twitterClient: TwitterRestClient = _
  @transient var streamingClient: TwitterStreamingClient[IO] = _
  @transient var running: Boolean = _

  override def open(parameters: Configuration): Unit = {
    twitterClient = TwitterRestClient(config)
    streamingClient = new TwitterStreamingClient[IO](config)
    running = true
  }

  override def run(ctx: SourceContext[TweetResponse]): Unit = {
    implicit val formats = DefaultFormats
    logger.info("Connecting to Twitter...")
    while (running) {
      val token = twitterClient.authenticate.unsafeRunSync()
      val jsonTweets = streamingClient.streamTweets(token.access_token)
      jsonTweets.map(json => {
        val tweet = parse(json).extract[TweetResponse]
        process(tweet)(ctx)
      }).compile.drain.unsafeRunSync
    }
  }

  override def cancel(): Unit = {
    logger.info("Closing connection to Twitter...")
    running = false
  }

  private def process(response: TweetResponse)(implicit ctx: SourceContext[TweetResponse]): Unit = {
    ctx.synchronized {
      ctx.collect(response)
    }
  }
}