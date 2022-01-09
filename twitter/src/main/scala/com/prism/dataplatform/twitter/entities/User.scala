package com.prism.dataplatform.twitter.entities

import java.time.Instant

final case class User(
                       id: String,
                       name: String,
                       username: String,
//                       created_at: Option[Instant],
//                       `protected`: Option[Boolean],
//                       withheld: Option[Withheld],
//                       location: Option[String],
//                       url: Option[String],
//                       description: Option[String],
                       verified: Option[Boolean],
//                       entities: Option[UserEntities],
//                       profile_image_url: Option[String],
                       public_metrics: Option[UserPublicMetrics],
//                       pinned_tweet_id: Option[String]
                     )

final case class UserPublicMetrics(
                                    followers_count: Option[Int],
                                    following_count: Option[Int],
                                    tweet_count: Option[Int],
                                    listed_count: Option[Int]
                                  )
