package com.prism.dataplatform.common.entities.enums

object TweetReplySetting extends Enumeration {
  type TweetReplySetting = Value

  val Everyone = Value("everyone")
  val MentionedUsers = Value("mentioned_users")
  val Followers = Value("followers")
}
