package com.prism.dataplatform.twitter.entities

final case class UserEntities(
                               url: Option[UserURLContainer],
                               description: Option[UserEntitiesDescription]
                             )

final case class UserURLContainer(urls: Array[UserEntitiesURL])

final case class UserEntitiesURL(
                                  start: Int,
                                  end: Int,
                                  url: String,
                                  expanded_url: String,
                                  display_url: String
                                )

final case class UserEntitiesDescription(
                                          urls: Array[UserEntitiesURL],
                                          hashtags: Array[UserEntitiesHashtag],
                                          mentions: Array[UserEntitiesMention],
                                          cashtags: Array[UserEntitiesCashtag]
                                        )

final case class UserEntitiesHashtag(
                                      start: Int,
                                      end: Int,
                                      tag: String
                                    )

final case class UserEntitiesMention(
                                      start: Int,
                                      end: Int,
                                      username: String
                                    )

final case class UserEntitiesCashtag(
                                      start: Int,
                                      end: Int,
                                      tag: String
                                    )
