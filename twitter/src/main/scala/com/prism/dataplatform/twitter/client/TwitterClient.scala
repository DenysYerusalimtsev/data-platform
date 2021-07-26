package com.prism.dataplatform.twitter.client

import cats.effect.IO
import com.prism.dataplatform.twitter.config.Constants.AUTH_API
import com.prism.dataplatform.twitter.config.TwitterConfig
import com.prism.dataplatform.twitter.entities.auth.AuthToken
import io.circe.generic.auto._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.dsl.io.POST
import org.http4s.headers.Authorization
import org.http4s._

import scala.concurrent.ExecutionContext.global

trait TwitterClient {
  private var _config: TwitterConfig = _
  implicit lazy val config: TwitterConfig = _config
  val httpClient = BlazeClientBuilder[IO](global).resource

  def setupSettings(config: TwitterConfig): Unit = {
    _config = config
  }

  def authenticate(): AuthToken = {

    implicit val tokenDecoder: EntityDecoder[IO, AuthToken] = circe.jsonOf[IO, AuthToken]

    val uri: Uri = Uri.fromString(AUTH_API).getOrElse(throw IllegalArgumentException)
    val headers = Headers(Authorization(BasicCredentials(config.consumerKey, config.consumerSecret)))
    val request = Request[IO](method = POST, uri = uri, headers = headers)

    httpClient.use(client => client.expect[AuthToken](request)).as[AuthToken]
  }
}
