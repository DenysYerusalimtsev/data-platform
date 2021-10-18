package com.prism.dataplatform.twitter.entities

final case class Tweet(
                        id: Option[String],
                        text: Option[String],
                        attachments: Option[TweetAttachments],
                        author_id: Option[String],
                        context_annotations: Option[Array[TweetContextAnnotation]],
                        conversation_id: Option[String],
                        created_at: Option[String],
                        entities: Option[TweetEntities],
                        geo: Option[TweetGeo],
                        in_reply_to_user_id: Option[String],
                        lang: Option[String],
                        organic_metrics: Option[TweetOrganicMetrics],
                        possibly_sensitive: Option[Boolean],
                        promoted_metrics: Option[TweetPromotedMetrics],
                        public_metrics: Option[TweetPublicMetrics],
                        referenced_tweets: Option[Array[TweetReferencedTweet]],
                        reply_settings: Option[String],
                        source: Option[String],
                        withheld: Option[Withheld]
                      )

final case class TweetTest(id: Option[String])

final case class TweetAttachments(
                                   media_keys: Option[Array[String]],
                                   poll_ids: Option[Array[String]]
                                 ) {
  def this() {
    this(media_keys = Some(Array.empty), poll_ids = Some(Array.empty))
  }
}

final case class TweetContextAnnotation(
                                         domain: Option[TweetDomain],
                                         entity: Option[TweetEntity]
                                       )

final case class TweetGeo(
                           coordinates: Option[TweetCoordinates],
                           place_id: Option[String]
                         )

final case class TweetCoordinates(
                                   `type`: Option[String],
                                   coordinates: Option[(Double, Double)]
                                 )

final case class TweetNonPublicMetrics(
                                        impression_count: Option[Int],
                                        url_link_clicks: Option[Int],
                                        user_profile_clicks: Option[Int]
                                      )

final case class TweetOrganicMetrics(
                                      impression_count: Option[Int],
                                      url_link_clicks: Option[Int],
                                      user_profile_clicks: Option[Int],
                                      retweet_count: Option[Int],
                                      reply_count: Option[Int],
                                      like_count: Option[Int]
                                    )

final case class TweetPromotedMetrics(
                                       impression_count: Option[Int],
                                       url_link_clicks: Option[Int],
                                       user_profile_clicks: Option[Int],
                                       retweet_count: Option[Int],
                                       reply_count: Option[Int],
                                       like_count: Option[Int]
                                     )

final case class TweetPublicMetrics(
                                     retweet_count: Option[Int],
                                     reply_count: Option[Int],
                                     like_count: Option[Int],
                                     quote_count: Option[Int]
                                   )

final case class TweetReferencedTweet(
                                       `type`: Option[String],
                                       id: Option[String]
                                     )

final case class TweetDomain(
                              id: Option[String],
                              name: Option[String],
                              description: Option[String]
                            )

final case class TweetEntity(
                              id: Option[String],
                              name: Option[String],
                              description: Option[String]
                            )
