package com.prism.dataplatform.common.entities.enums

object ReferencedTweetType extends Enumeration {
  type ReferencedTweetType = Value

  val Retweeted = Value("retweeted")
  val Quoted = Value("quoted")
  val RepliedTo = Value("replied_to")
}
