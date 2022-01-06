package com.prism.dataplatform.twitter.client

import cats.effect._
import com.prism.dataplatform.twitter.config.Constants.SEARCH_TWEETS_STREAM_API
import com.prism.dataplatform.twitter.config.TwitterConfig
import com.prism.dataplatform.twitter.utils.TwitterUtils.UriQueryParametersBuilder
import fs2.Stream
import io.circe.Json
import io.circe.jawn.CirceSupportParser
import org.http4s.Method.GET
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.headers.Authorization
import org.typelevel.jawn.fs2.JsonStreamSyntax

import scala.concurrent.ExecutionContext.global
import scala.language.higherKinds

class TwitterStreamingClient[F[_]](config: TwitterConfig)(implicit F: Async[F], cs: Spawn[F]) {
  implicit val facade = CirceSupportParser.facade

  def filteredStream(token: String): Stream[F, Json] = {
    val uri: Uri = Uri.fromString(SEARCH_TWEETS_STREAM_API).getOrElse(new Uri())
      .withTweetFields
      .withMediaFields
      .withPlaceFields
      .withPollFields
      .withUserFields
      .withExpansions

    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[F](method = GET, uri = uri, headers = headers)
    for {
      client <- BlazeClientBuilder[F].withExecutionContext(global).stream
      response <- client.stream(request).flatMap(_.body.chunks.parseJsonStream)
    } yield response
  }

  def streamTweets(token: String): Stream[F, String] = {
    filteredStream(token).map(_.spaces2)
  }
}
