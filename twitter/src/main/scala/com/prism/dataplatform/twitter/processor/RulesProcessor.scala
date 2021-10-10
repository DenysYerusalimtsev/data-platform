package com.prism.dataplatform.twitter.processor

import com.prism.dataplatform.twitter.entities.Rule
import com.prism.dataplatform.twitter.entities.responses.RulesResponse

trait RulesProcessor {
  def addRules(rule: String): Rule =
    Rule(value = s"$rule OR #$rule", tag = Some(rule + " with content"))
  //      .withLinks
  //      .withMedia
  //      .withImages
  //      .withMentions
  //      .withRetweets

  def areRulesExists(rules: RulesResponse): Boolean =
    rules.data.nonEmpty
}
