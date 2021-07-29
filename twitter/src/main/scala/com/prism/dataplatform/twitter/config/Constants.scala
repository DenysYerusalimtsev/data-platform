package com.prism.dataplatform.twitter.config

object Constants {
  final val CONSUMER_KEY = "twitter-source.consumerKey"
  final val CONSUMER_SECRET = "twitter-source.consumerSecret"
  final val BEARER_TOKEN = "bearer"
  final val TOKEN = "twitter-source.token"
  final val TOKEN_SECRET = "twitter-source.tokenSecret"

  final val AUTH_API = "https://api.twitter.com/oauth2/token"
  final val RECENT_TWEETS_API = "https://api.twitter.com/2/tweets/counts/recent"
  final val SEARCH_TWEETS_API = "https://api.twitter.com/2/tweets/search/recent"

  final val GRANULARITY = "day"
  final val TWEET_FIELDS = ""
  final val MEDIA_FIELDS = ""
  final val PLACE_FIELDS = ""
  final val POLL_FIELDS = ""
  final val USER_FIELDS = ""
  final val EXPANSIONS = ""
}
