package com.prism.dataplatform.twitter.client

import cats.effect.IO
import com.prism.dataplatform.twitter.config.Constants._
import com.prism.dataplatform.twitter.config.TwitterConfig
import com.prism.dataplatform.twitter.entities.auth.AuthToken
import com.prism.dataplatform.twitter.entities.responses.{TweetCountResponse, TweetResponse}
import io.circe.generic.auto._
import org.http4s.Method.{GET, POST}
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.headers.Authorization

import scala.concurrent.ExecutionContext.global

trait TwitterClient {
  private var _twitterConfig: TwitterConfig = _
  lazy val twitterConfig: TwitterConfig = _twitterConfig
  val httpClient = BlazeClientBuilder[IO](global).resource

  def setupSettings(config: TwitterConfig): Unit = {
    _twitterConfig = config
  }

  def authenticate(): IO[AuthToken] = {
    implicit val tokenDecoder: EntityDecoder[IO, AuthToken] = circe.jsonOf[IO, AuthToken]

    val uri: Uri = Uri.fromString(AUTH_API).getOrElse(new Uri())
      .withQueryParam("grant_type", "client_credentials")
    val headers = Headers(Authorization(BasicCredentials(twitterConfig.consumerKey, twitterConfig.consumerSecret)))
    val request = Request[IO](method = POST, uri = uri, headers = headers)

    httpClient.use(client => client.expect[AuthToken](request))
  }

  def getTweetsCount(search: String, token: String): IO[TweetCountResponse] = {
    implicit val tweetCountDecoder: EntityDecoder[IO, TweetCountResponse] = circe.jsonOf[IO, TweetCountResponse]

    val uri: Uri = Uri.fromString(RECENT_TWEETS_API).getOrElse(new Uri())
      .withQueryParam("query", search)
      .withQueryParam("granularity", GRANULARITY)
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[IO](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[TweetCountResponse](request))
  }

  def searchTweets(search: String, token: String): IO[TweetResponse] = {
    implicit val tweetDecoder: EntityDecoder[IO, TweetResponse] = circe.jsonOf[IO, TweetResponse]

    val uri: Uri = Uri.fromString(SEARCH_TWEETS_API).getOrElse(new Uri())
      .withQueryParam("query", search)
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[IO](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[TweetResponse](request))
  }
}
