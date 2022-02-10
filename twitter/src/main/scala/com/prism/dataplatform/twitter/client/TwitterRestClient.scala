package com.prism.dataplatform.twitter.client

import cats.effect.Async
import cats.effect.kernel.Resource
import com.prism.dataplatform.twitter.config.Constants._
import com.prism.dataplatform.twitter.entities.auth.AuthToken
import com.prism.dataplatform.twitter.entities.requests.{AddRules, DeleteRule}
import com.prism.dataplatform.twitter.entities.responses._
import com.prism.dataplatform.twitter.utils.TwitterUtils.UriQueryParametersBuilder
import io.circe.generic.auto._
import org.http4s.Method.{GET, POST}
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.client.Client
import org.http4s.headers.Authorization

import scala.concurrent.ExecutionContext.global
import scala.language.higherKinds

case class TwitterRestClient[F[_]](consumerKey: String,
                                   consumerSecret: String)(implicit F: Async[F]) {
  val httpClient: Resource[F, Client[F]] = BlazeClientBuilder[F].withExecutionContext(global).resource

  def authenticate(): F[AuthToken] = {
    implicit val tokenDecoder: EntityDecoder[F, AuthToken] = circe.jsonOf[F, AuthToken]

    val uri: Uri = Uri.fromString(AUTH_API).getOrElse(new Uri())
      .withQueryParam("grant_type", "client_credentials")
    val headers = Headers(Authorization(BasicCredentials(consumerKey, consumerSecret)))
    val request = Request[F](method = POST, uri = uri, headers = headers)

    httpClient.use(client => client.expect[AuthToken](request))
  }

  def countTweets(search: String, token: String): F[TweetCountResponse] = {
    implicit val tweetCountDecoder: EntityDecoder[F, TweetCountResponse] = circe.jsonOf[F, TweetCountResponse]

    val uri: Uri = Uri.fromString(RECENT_TWEETS_API).getOrElse(new Uri())
      .withQuery(search)
      .withQueryParam("granularity", GRANULARITY)
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[F](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[TweetCountResponse](request))
  }

  def searchTweets(search: String, token: String): F[TweetsResponse] = {
    implicit val tweetsDecoder: EntityDecoder[F, TweetsResponse] = circe.jsonOf[F, TweetsResponse]

    val uri: Uri = Uri.fromString(SEARCH_TWEETS_API).getOrElse(new Uri())
      .withQuery(search)
      .withTweetFields
      .withMediaFields
      .withPlaceFields
      .withPollFields
      .withUserFields
      .withExpansions
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[F](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[TweetsResponse](request))
  }

  def retrieveRules(token: String): F[RulesResponse] = {
    implicit val rulesDecoder: EntityDecoder[F, RulesResponse] = circe.jsonOf[F, RulesResponse]
    val uri: Uri = Uri.fromString(STREAM_RULES_API).getOrElse(new Uri())
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[F](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[RulesResponse](request))
  }

  def applyRules(rules: AddRules, token: String): F[AddRulesResponse] = {
    implicit val rulesDecoder: EntityDecoder[F, AddRulesResponse] = circe.jsonOf[F, AddRulesResponse]

    val uri: Uri = Uri.fromString(STREAM_RULES_API).getOrElse(new Uri())
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[F](method = POST, uri = uri, headers = headers)
      .withEntity(rules)

    httpClient.use(client => client.expect[AddRulesResponse](request))
  }

  def deleteRules(rules: DeleteRule, token: String): F[RulesResponse] = {
    implicit val rulesDecoder: EntityDecoder[F, RulesResponse] = circe.jsonOf[F, RulesResponse]

    val uri: Uri = Uri.fromString(STREAM_RULES_API).getOrElse(new Uri())
    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[F](method = POST, uri = uri, headers = headers)
      .withEntity(rules)

    httpClient.use(client => client.expect[RulesResponse](request))
  }

  def filteredStream(token: String): F[TweetsResponse] = {
    implicit val tweetsDecoder: EntityDecoder[F, TweetsResponse] = circe.jsonOf[F, TweetsResponse]

    val uri: Uri = Uri.fromString(SEARCH_TWEETS_STREAM_API).getOrElse(new Uri())
      .withTweetFields
      .withMediaFields
      .withPlaceFields
      .withPollFields
      .withUserFields
      .withExpansions

    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[F](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[TweetsResponse](request))
  }

  def filteredStringStream(token: String): F[String] = {
    implicit val tweetsDecoder: EntityDecoder[F, TweetsResponse] = circe.jsonOf[F, TweetsResponse]

    val uri: Uri = Uri.fromString(SEARCH_TWEETS_STREAM_API).getOrElse(new Uri())
      .withTweetFields
      .withMediaFields
      .withPlaceFields
      .withPollFields
      .withUserFields
      .withExpansions

    val headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, token)))
    val request = Request[F](method = GET, uri = uri, headers = headers)

    httpClient.use(client => client.expect[String](request))
  }
}