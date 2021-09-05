package com.prism.dataplatform.twitter.processor

import com.prism.dataplatform.twitter.entities.Rule
import com.prism.dataplatform.twitter.entities.responses.RulesResponse
import com.prism.dataplatform.twitter.utils.TwitterUtils.RuleBuilder

trait RulesProcessor {
  def addRules(rule: String): Rule =
    Rule(value = Some(s"$rule OR #$rule"), tag = Some(rule + "with content"), id = null)
//      .withLinks
//      .withMedia
//      .withImages
//      .withMentions
//      .withRetweets

  def areRulesExists(rules: RulesResponse): Boolean =
    rules.data.nonEmpty
}
