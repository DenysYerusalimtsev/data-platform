package com.prism.dataplatform.twitter.entities.enums

object WithheldScope extends Enumeration {
  type WithheldScope = Value

  val Tweet = Value("tweet")
  val User = Value("user")
}
