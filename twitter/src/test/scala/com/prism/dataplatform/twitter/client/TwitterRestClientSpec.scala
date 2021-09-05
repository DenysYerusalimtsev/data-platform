package com.prism.dataplatform.twitter.client

import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.core.config.YamlConfigProvider
import com.prism.dataplatform.twitter.BaseTest
import com.prism.dataplatform.twitter.config.{Config, TwitterConfig}
import com.prism.dataplatform.twitter.entities.Rule
import com.prism.dataplatform.twitter.entities.requests.AddRules
import com.prism.dataplatform.twitter.processor.RulesProcessor

class TwitterRestClientSpec extends BaseTest {
  behavior of classOf[TwitterRestClient].getSimpleName

  val rulesProcessor = new RulesProcessor {}
  val twitterClient = new TwitterRestClient {}
  val twitterConfig = new TwitterConfig()
  val configProvider = new YamlConfigProvider {}
  val config: Config = configProvider.configFrom[Config]("D:\\Projects\\Prism-dp\\data-platform\\twitter\\src\\test\\resources\\twitter.yaml")
  twitterClient.setupSettings(config.twitter)

  it should "successfully creates and applies new rules" in {

    val rules: AddRules = AddRules(Seq[Rule](rulesProcessor.addRules("spacex")))

    val testCase = for {
      token <- twitterClient.authenticate
      r <- twitterClient.applyRules(token.access_token, rules)
    } yield r

    testCase.map(resp => resp.meta.get)
    testCase.unsafeRunSync()

//    testCase.map(resp => assert(resp.meta.nonEmpty)).unsafeRunSync()
  }
}
