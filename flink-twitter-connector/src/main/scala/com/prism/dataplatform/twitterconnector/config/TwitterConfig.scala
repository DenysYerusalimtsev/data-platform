package com.prism.dataplatform.twitterconnector.config

object Twitter {
  case class TwitterConfig(
                            consumerKey: String,
                            consumerSecret: String,
                            bearerToken: String,
                            token: String,
                            tokenSecret: String
                          )
}
