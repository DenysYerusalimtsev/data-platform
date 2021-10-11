package com.prism.dataplatform.twitter

import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.config.{Config, TConfig}
import com.prism.dataplatform.twitter.entities.responses.TweetsResponse
import com.prism.dataplatform.twitterconnector.Twitter
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.DataStream

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

    //    val twitterClient = TwitterRestClient(config.twitter)

//    val rules: AddRules = AddRules(Seq[Rule](Rule("spacex", None)))
//    val program = for {
//      token <- twitterClient.authenticate
//      tweetsString <- twitterClient.filteredStringStream(token.access_token)
//
//    } yield tweets
//
//    program.map(resp => resp.data.map(t => println(t))).unsafeRunSync()
  }

}

//TO DO: cache
// Source -> flatMap monads