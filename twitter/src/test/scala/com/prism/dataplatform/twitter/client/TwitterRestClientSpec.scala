package com.prism.dataplatform.twitter.client

import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.core.config.YamlConfigProvider
import com.prism.dataplatform.twitter.BaseTest
import com.prism.dataplatform.twitter.config.{Config, TwitterConfig}
import com.prism.dataplatform.common.entities.{Rule, RuleDestruction}
import com.prism.dataplatform.common.entities.requests.{AddRules, DeleteRule}
import com.prism.dataplatform.twitter.processor.RulesProcessor

class TwitterRestClientSpec extends BaseTest {
  behavior of classOf[TwitterRestClient].getSimpleName

  val rulesProcessor = new RulesProcessor {}
  val twitterConfig = new TwitterConfig()
  val configProvider = new YamlConfigProvider {}
  val config: Config = configProvider.configFrom[Config]("D:\\Projects\\Prism-dp\\data-platform\\twitter\\src\\test\\resources\\twitter.yaml")
  val twitterClient = TwitterRestClient(config.twitter)

  it should "successfully authenticate in Twitter" in {

    val testCase = for {
      token <- twitterClient.authenticate
    } yield token

    testCase.map(resp => assert(resp.access_token.nonEmpty)).unsafeRunSync()
  }

  it should "successfully count tweets with selected topic" in {

    val testCase = for {
      token <- twitterClient.authenticate
      tweets <- twitterClient.countTweets("spacex", token.access_token)
    } yield tweets

    testCase.map(resp => assert(resp.data.nonEmpty)).unsafeRunSync()
  }

  it should "successfully search tweets with selected topic" in {

    val testCase = for {
      token <- twitterClient.authenticate
      tweets <- twitterClient.searchTweets("spacex", token.access_token)
    } yield tweets

    testCase.map(resp => {
      assert(resp.data.nonEmpty)
      assert(resp.meta.nonEmpty)
      assert(resp.errors.isEmpty)
    }).unsafeRunSync()
  }

  it should "successfully creates and applies new rules" in {

    val rules: AddRules = AddRules(Seq[Rule](rulesProcessor.addRules("spacex")))

    val testCase = for {
      token <- twitterClient.authenticate
      rule <- twitterClient.applyRules(rules, token.access_token)
    } yield rule

    testCase.map(resp => resp.meta.map(ruleMeta =>
      assert(ruleMeta.sent.nonEmpty))).unsafeRunSync()
  }

  it should "successfully retrieve rules applied on Twitter Stream" in {

    val testCase = for {
      token <- twitterClient.authenticate
      rules <- twitterClient.retrieveRules(token.access_token)
    } yield rules

    testCase.map(resp => {
      assert(resp.data.nonEmpty)
      assert(resp.meta.nonEmpty)
    }).unsafeRunSync()
  }

  it should "successfully delete applied rule from Twitter" in {

    val rules: AddRules = AddRules(Seq[Rule](rulesProcessor.addRules("musk")))
    val destructor = RuleDestruction(ids = None, values = Seq[String]("musk OR #musk"))
    val deleteRequest = DeleteRule(destructor)
    val testCase = for {
      token <- twitterClient.authenticate
      _ <- twitterClient.applyRules(rules, token.access_token)
      destruct <- twitterClient.deleteRules(deleteRequest, token.access_token)
    } yield destruct

    testCase.map(resp => resp.meta.map(ruleMeta =>
      assert(ruleMeta.sent.nonEmpty))).unsafeRunSync()
  }
}
