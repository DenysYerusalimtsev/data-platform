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
  final val STREAM_RULES_API = "https://api.twitter.com/2/tweets/search/stream/rules"
  final val SEARCH_TWEETS_STREAM_API = "https://api.twitter.com/2/tweets/search/stream"


  final val QUERY = "query"
  final val GRANULARITY = "day"
  final val TWEET_FIELDS = "tweet.fields"
  final val MEDIA_FIELDS = "media.fields"
  final val PLACE_FIELDS = "place.fields"
  final val POLL_FIELDS = "poll.fields"
  final val USER_FIELDS = "user.fields"
  final val EXPANSIONS = "expansions"
}
