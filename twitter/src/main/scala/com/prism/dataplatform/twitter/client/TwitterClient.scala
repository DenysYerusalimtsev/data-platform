package com.prism.dataplatform.twitter.client

import cats.effect.IO
import com.prism.dataplatform.twitter.entities.Tweet
import org.http4s.EntityEncoder

trait TwitterClient {
  implicit def tweetEncoder: EntityEncoder[IO, Tweet] = ???
}
