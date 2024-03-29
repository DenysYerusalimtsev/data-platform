package com.prism.dataplatform.twitter.entities

final case class TweetEntities(
                                annotations: Option[Array[TweetEntitiesAnnotation]],
                                urls: Option[Array[TweetEntitiesURL]],
                                hashtags: Option[Array[TweetEntitiesHashtag]],
                                mentions: Option[Array[TweetEntitiesMention]],
                                cashtags: Option[Array[TweetEntitiesCashtag]]
                              )

final case class TweetEntitiesAnnotation(
                                          start: Option[Int],
                                          end: Option[Int],
                                          probability: Option[Float],
                                          `type`: Option[String],
                                          normalized_text: Option[String]
                                        )

final case class TweetEntitiesURL(
                                   start: Option[Int],
                                   end: Option[Int],
                                   url: Option[String],
                                   expanded_url: Option[String],
                                   display_url: Option[String],
                                   unwound_url: Option[String]
                                 )

final case class TweetEntitiesHashtag(
                                       start: Option[Int],
                                       end: Option[Int],
                                       tag: Option[String]
                                     )

final case class TweetEntitiesMention(
                                       start: Option[Int],
                                       end: Option[Int],
                                       username: Option[String],
                                       id: Option[String]
                                     )

final case class TweetEntitiesCashtag(
                                       start: Option[Int],
                                       end: Option[Int],
                                       tag: Option[String]
                                     )
