package com.prism.dataplatform.twitter.client

import cats.effect.IO
import com.prism.dataplatform.twitter.config.Constants.AUTH_API
import com.prism.dataplatform.twitter.config.TwitterConfig
import com.prism.dataplatform.twitter.entities.auth.AuthToken
import io.circe.generic.auto._
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.dsl.io.POST
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

    val uri: Uri = Uri.fromString(AUTH_API).getOrElse(new Uri()).withQueryParam("grant_type", "client_credentials")
    val headers = Headers(Authorization(BasicCredentials(twitterConfig.consumerKey, twitterConfig.consumerSecret)))
    val request = Request[IO](method = POST, uri = uri, headers = headers)

    httpClient.use(client => client.expect[AuthToken](request))
  }
}
