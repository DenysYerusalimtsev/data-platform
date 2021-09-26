package com.prism.dataplatform.common.entities

final case class TweetIncludes(
                                tweets: Option[Seq[Tweet]]
                                // users: Seq[User], // TODO: Pending addition of users model
                                // places: Seq[Place], // TODO: Pending addition of places model
                                // media: Seq[Media], // TODO: Pending addition of media model
                                // polls: Seq[Polls] // TODO pending addition of polls model
                              )
