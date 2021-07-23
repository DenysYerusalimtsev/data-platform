package com.prism.dataplatform.twitter.entities.enums

object TweetReplySetting extends Enumeration {
  type TweetReplySetting = Value

  val Everyone = Value("everyone")
  val MentionedUsers = Value("mentioned_users")
  val Followers = Value("followers")
}
