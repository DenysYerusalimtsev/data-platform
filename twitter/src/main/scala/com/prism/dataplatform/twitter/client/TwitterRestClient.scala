package com.prism.dataplatform.twitter.client

import cats.effect.IO
import cats.effect.kernel.Resource
import com.prism.dataplatform.twitter.config.Constants._
import com.prism.dataplatform.twitter.config.{TConfig, TwitterConfig}
import com.prism.dataplatform.twitter.entities.auth.AuthToken
import com.prism.dataplatform.twitter.entities.requests.{AddRules, DeleteRule}
import com.prism.dataplatform.twitter.entities.responses.{AddRulesResponse, RulesResponse, TweetCountResponse, TweetsResponse}
import com.prism.dataplatform.twitter.utils.TwitterUtils.UriQueryParametersBuilder
import io.circe.generic.auto._
import org.http4s.Method.{GET, POST}
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.client.Client
import org.http4s.headers.Authorization

import scala.concurrent.ExecutionContext.global

case class TwitterRestClient(config: TConfig) {
  val httpClient: Resource[IO, Client[IO]] = BlazeClientBuilder[IO](global).resource

  def authenticate(): IO[AuthToken] = {
    implicit val tokenDecoder: EntityDecoder[IO, AuthToken] = circe.jsonOf[IO, AuthToken]

    val uri: Uri = Uri.fromString(AUTH_API).getOrElse(new Uri())
      .withQueryParam("grant_type", "client_credentials")
    val headers = Headers(Authorization(BasicCredentials(config.consumerKey, config.consumerSecret)))
    val request = Request[IO](method = POST, uri = uri, headers = headers)

    httpClient.use(client => client.expect[AuthToken](request))
  }

  def countTweets(search: String, token: String): IO[TweetCountResponse] = {
    implicit val tweetCountDecoder: EntityDecoder[IO, TweetCountResponse] = circe.jsonOf[IO, TweetCountResponse]

    val uri: Uri = Uri.fromString(RECENT_TWEETS_API).getOrElse(new Uri())
      .withQuery(search)
      .withQueryParam("granularity", GRANULARITY)
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[IO](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[TweetCountResponse](request))
  }

  def searchTweets(search: String, token: String): IO[TweetsResponse] = {
    implicit val tweetsDecoder: EntityDecoder[IO, TweetsResponse] = circe.jsonOf[IO, TweetsResponse]

    val uri: Uri = Uri.fromString(SEARCH_TWEETS_API).getOrElse(new Uri())
      .withQuery(search)
      .withTweetFields
      .withMediaFields
      .withPlaceFields
      .withPollFields
      .withUserFields
      .withExpansions
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[IO](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[TweetsResponse](request))
  }

  def retrieveRules(token: String): IO[RulesResponse] = {
    implicit val rulesDecoder: EntityDecoder[IO, RulesResponse] = circe.jsonOf[IO, RulesResponse]
    val uri: Uri = Uri.fromString(STREAM_RULES_API).getOrElse(new Uri())
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[IO](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[RulesResponse](request))
  }

  def applyRules(rules: AddRules, token: String): IO[AddRulesResponse] = {
    implicit val rulesDecoder: EntityDecoder[IO, AddRulesResponse] = circe.jsonOf[IO, AddRulesResponse]

    val uri: Uri = Uri.fromString(STREAM_RULES_API).getOrElse(new Uri())
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[IO](method = POST, uri = uri, headers = headers)
      .withEntity(rules)

    httpClient.use(client => client.expect[AddRulesResponse](request))
  }

  def deleteRules(rules: DeleteRule, token: String): IO[RulesResponse] = {
    implicit val rulesDecoder: EntityDecoder[IO, RulesResponse] = circe.jsonOf[IO, RulesResponse]

    val uri: Uri = Uri.fromString(STREAM_RULES_API).getOrElse(new Uri())
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[IO](method = POST, uri = uri, headers = headers)
      .withEntity(rules)

    httpClient.use(client => client.expect[RulesResponse](request))
  }

  def filteredStream(token: String): IO[TweetsResponse] = {
    implicit val tweetsDecoder: EntityDecoder[IO, TweetsResponse] = circe.jsonOf[IO, TweetsResponse]

    val uri: Uri = Uri.fromString(SEARCH_TWEETS_STREAM_API).getOrElse(new Uri())
      .withTweetFields
      .withMediaFields
      .withPlaceFields
      .withPollFields
      .withUserFields
      .withExpansions


    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[IO](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[TweetsResponse](request))
  }

  def filteredStringStream(token: String): IO[String] = {
    implicit val tweetsDecoder: EntityDecoder[IO, TweetsResponse] = circe.jsonOf[IO, TweetsResponse]

    val uri: Uri = Uri.fromString(SEARCH_TWEETS_STREAM_API).getOrElse(new Uri())
      .withTweetFields
      .withMediaFields
      .withPlaceFields
      .withPollFields
      .withUserFields
      .withExpansions


    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[IO](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[String](request))
  }
}
