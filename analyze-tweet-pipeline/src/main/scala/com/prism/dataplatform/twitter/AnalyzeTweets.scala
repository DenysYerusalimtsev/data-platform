package com.prism.dataplatform.twitter

import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.twitter.entities.Rule
import com.prism.dataplatform.twitter.entities.requests.AddRules
import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.client.TwitterRestClient
import com.prism.dataplatform.twitter.config.Config

final class AnalyzeTweets extends FlinkJob[Config] {
  override def script(): Unit = {
    logger.info("Application started")
    val twitterClient = TwitterRestClient(config.twitter)

    val rules: AddRules = AddRules(Seq[Rule](Rule("spacex", None)))
    val program = for {
      token <- twitterClient.authenticate
      tweets <- twitterClient.filteredStringStream(token.access_token)
    } yield tweets

    program.map(resp => logger.error(resp))
      .unsafeRunSync()
  }
}

//TO DO: cache
// Source -> flatMap monads