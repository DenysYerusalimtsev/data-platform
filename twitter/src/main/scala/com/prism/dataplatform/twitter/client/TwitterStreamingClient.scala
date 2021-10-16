package com.prism.dataplatform.twitter.client

import cats.effect._
import com.prism.dataplatform.twitter.config.Constants.SEARCH_TWEETS_STREAM_API
import com.prism.dataplatform.twitter.config.TConfig
import com.prism.dataplatform.twitter.entities.responses.TweetsResponse
import fs2.Stream
import io.circe.Decoder.Result
import io.circe.Json
import io.circe.generic.auto.exportDecoder
import io.circe.jawn.CirceSupportParser
import org.http4s.Method.GET
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.oauth1
import org.http4s.headers.Authorization
import org.typelevel.jawn.fs2.JsonStreamSyntax

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global
import scala.language.higherKinds

class TwitterStreamingClient[F[_]](config: TConfig)(implicit F: Async[F], cs: Spawn[F]) {
  implicit val f = CirceSupportParser.facade

  def filteredStream(token: String): Stream[F, Json] = {
    val uri: Uri = Uri.fromString(SEARCH_TWEETS_STREAM_API).getOrElse(new Uri())
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[F](method = GET, uri = uri, headers = headers)
    for {
      client <- BlazeClientBuilder(global).stream
      response <- client.stream(request).flatMap(_.body.chunks.parseJsonStream)
    } yield response
  }

  def streamTweets(token: String): Stream[F, TweetsResponse] = {

    val stream = filteredStream(token)
    stream.map(json => {
      val mapResult: Result[TweetsResponse] = json.as[TweetsResponse]
      mapResult.getOrElse(throw new RuntimeException("Fail to parse"))
    })
  }

  //
  //    def blockingEcStream: Stream[F, ExecutionContext] =
  //      Stream.bracket(F.delay(Executors.newFixedThreadPool(4)))(pool =>
  //        F.delay(pool.shutdown()))
  //        .map(ExecutionContext.fromExecutorService)
  //
  //    /** Compile our stream down to an effect to make it runnable */
  //    def run: F[Unit] =
  //      blockingEcStream.flatMap { blockingEc =>
  //        streamTweets(blockingEc)
  //      }.compile.drain
}
