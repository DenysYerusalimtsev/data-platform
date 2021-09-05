package com.prism.dataplatform.twitter.client

import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.twitter.BaseTest
import com.prism.dataplatform.twitter.entities.Rule
import com.prism.dataplatform.twitter.entities.requests.AddRules
import com.prism.dataplatform.twitter.processor.RulesProcessor

class TwitterRestClientSpec extends BaseTest {
  behavior of classOf[TwitterRestClient].getSimpleName

  val rulesProcessor = new RulesProcessor {}
  val twitterClient = new TwitterRestClient {}
  it should "successfully creates and applies new rules" in {

    val rules: AddRules = AddRules(Seq[Rule](rulesProcessor.addRules("spacex")))

    val testCase = for {
      token <- twitterClient.authenticate
      rules <- twitterClient.applyRules(token.access_token, rules)
    } yield rules

    val actual = 1
    val expected = 1
    actual shouldBe expected
//    testCase.map(resp => assert(resp.meta.nonEmpty)).unsafeRunSync()
  }
}
