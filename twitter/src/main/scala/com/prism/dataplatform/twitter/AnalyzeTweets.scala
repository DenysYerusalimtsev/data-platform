package com.prism.dataplatform.twitter

import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.client.TwitterRestClient
import com.prism.dataplatform.twitter.config.Config
import com.prism.dataplatform.common.entities.Rule
import com.prism.dataplatform.common.entities.requests.AddRules
import com.prism.dataplatform.twitter.processor.RulesProcessor

final class AnalyzeTweets extends FlinkJob[Config]
  with RulesProcessor {
  override def script(): Unit = {
    logger.info("Application started")
    val twitterClient = TwitterRestClient(config.twitter)

    val rules: AddRules = AddRules(Seq[Rule](addRules("spacex")))
    val program = for {
      token <- twitterClient.authenticate
      tweets <- twitterClient.filteredStream(token.access_token)
    } yield tweets

    program.map(resp => resp.data.map(tweet => println(tweet)))
      .unsafeRunSync()
  }
}

//TO DO: cache
// Source -> flatMap monads