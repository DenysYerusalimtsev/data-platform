package com.prism.dataplatform.twitter

import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.flink.FlinkJob
import com.prism.dataplatform.twitter.client.TwitterRestClient
import com.prism.dataplatform.twitter.config.Config
import com.prism.dataplatform.twitter.entities.Rule
import com.prism.dataplatform.twitter.entities.requests.AddRules
import com.prism.dataplatform.twitter.processor.RulesProcessor

final class AnalyzeTweets extends FlinkJob[Config]
  with TwitterRestClient
  with RulesProcessor {
  override def script(): Unit = {
    logger.info("Application started")
    setupSettings(config.twitter)

    val rules: AddRules = AddRules(Seq[Rule](addRules("spacex")))
    val program = for {
      token <- authenticate
      rules <- applyRules(token.access_token, rules)
      tweets <- filteredStream(token.access_token)
    } yield tweets

    program.unsafeRunSync()
    print(program)
  }
}

//TO DO: cache
// Source -> flatMap monads