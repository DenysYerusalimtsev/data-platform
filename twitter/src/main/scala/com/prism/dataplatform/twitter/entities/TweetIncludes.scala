package com.prism.dataplatform.twitter.entities

final case class TweetIncludes(
                                tweets: Option[Array[Tweet]],
                                users: Option[Array[User]]
                                // places: Seq[Place], // TODO: Pending addition of places model
                                // media: Seq[Media], // TODO: Pending addition of media model
                                // polls: Seq[Polls] // TODO pending addition of polls model
                              )
