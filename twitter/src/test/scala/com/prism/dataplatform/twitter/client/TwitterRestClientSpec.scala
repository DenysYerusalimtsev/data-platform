package com.prism.dataplatform.twitter.client

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.core.config.YamlConfigProvider
import com.prism.dataplatform.twitter.BaseTest
import com.prism.dataplatform.twitter.config.TwitterProperties
import com.prism.dataplatform.twitter.entities.requests.{AddRules, DeleteRule}
import com.prism.dataplatform.twitter.entities.{Rule, RuleDestruction}
import com.prism.dataplatform.twitter.processor.RulesProcessor

class TwitterRestClientSpec extends BaseTest {
  behavior of classOf[TwitterRestClient[IO]].getSimpleName

  val rulesProcessor = new RulesProcessor {}
  val twitterConfig = new TwitterProperties()
  val configProvider = new YamlConfigProvider {}
  val twitterClient = TwitterRestClient[IO](
    consumerKey = "iKU3YJv4oHqq3Tx7DXKBW0D27",
    consumerSecret = "trSdfKpVpqHCZF7pfBuJxXSK0ZeU5Qfz9QYWUxBjVFoM6uTuYS")
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

    val rules: AddRules = AddRules(Seq[Rule](
      rulesProcessor.addRules("spacex"),
      rulesProcessor.addRules("crypto"),
      rulesProcessor.addRules("dogecoin"),
      rulesProcessor.addRules("ukraine"),
      rulesProcessor.addRules("metaverse"),
      rulesProcessor.addRules("tesla")
    ))

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

//  it should "successfully delete applied rule from Twitter" in {
//
//    val rules: AddRules = AddRules(Seq[Rule](rulesProcessor.addRules("musk")))
//    val destructor = RuleDestruction(ids = None, values = Array[String]("musk OR #musk"))
//    val deleteRequest = DeleteRule(destructor)
//    val testCase = for {
//      token <- twitterClient.authenticate
//      _ <- twitterClient.applyRules(rules, token.access_token)
//      destruct <- twitterClient.deleteRules(deleteRequest, token.access_token)
//    } yield destruct
//
//    testCase.map(resp => resp.meta.map(ruleMeta =>
//      assert(ruleMeta.sent.nonEmpty))).unsafeRunSync()
//  }

//  it should "successfully get filtered stream from Twitter" in {
//
//    val rules: AddRules = AddRules(Seq[Rule](rulesProcessor.addRules("covid19")))
//    val testCase = for {
//      token <- twitterClient.authenticate
//      _ <- twitterClient.applyRules(rules, token.access_token)
//      tweets <- twitterClient.filteredStream(token.access_token)
//    } yield tweets
//
//    testCase.map(resp => resp.data.map(data =>
//      println(data))).unsafeRunSync()
//  }

//  it should "successfully get filtered string stream from Twitter" in {
//
//    val rules: AddRules = AddRules(Seq[Rule](rulesProcessor.addRules("covid19")))
//    val testCase = for {
//      token <- twitterClient.authenticate
//      _ <- twitterClient.applyRules(rules, token.access_token)
//      tweets <- twitterClient.filteredStringStream(token.access_token)
//    } yield tweets
//
//    testCase.map(resp => assert(resp.nonEmpty))
//      .unsafeRunSync()
//  }
}
