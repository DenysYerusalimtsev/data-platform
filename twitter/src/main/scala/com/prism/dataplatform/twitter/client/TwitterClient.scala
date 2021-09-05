package com.prism.dataplatform.twitter.client

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.prism.dataplatform.twitter.processor.RulesProcessor

trait TwitterClient extends TwitterRestClient
  with RulesProcessor {
  def connectToStream(): Unit = {
    // TODO: check if existing rules exist
    // remove existing rules if they exist
    // setup new rules
    // connect to the stream
//    val exists =
//    authenticate
//      .flatMap(token => retrieveRules(token.access_token)
//        .flatMap(rules => areRulesExists(rules)))

  }
}
