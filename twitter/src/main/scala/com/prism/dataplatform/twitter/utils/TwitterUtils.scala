package com.prism.dataplatform.twitter.utils

import com.prism.dataplatform.twitter.config.Constants._
import com.prism.dataplatform.twitter.entities.enums.expansions.TweetExpansions
import com.prism.dataplatform.twitter.entities.enums.fields._
import org.http4s.Uri

object TwitterUtils {

  implicit class UriQueryParametersBuilder(uri: Uri) {
    def withQuery(search: String): Uri = {
      uri.withQueryParam(QUERY, search)
    }

    def withTweetFields(): Uri = {
      val params = List(
        TweetFields.Attachments,
        TweetFields.AuthorId,
        TweetFields.ContextAnnotations,
        TweetFields.ConversationId,
        TweetFields.CreatedAt,
        TweetFields.Entities,
        TweetFields.Geo,
        TweetFields.Id,
        TweetFields.InReplyToUserId,
        TweetFields.Lang,
        TweetFields.PossiblySensitive,
        TweetFields.PublicMetrics,
        TweetFields.ReferencedTweets,
        TweetFields.ReplySettings,
        TweetFields.Source,
        TweetFields.Text,
        TweetFields.Withheld
      )

      uri.withQueryParam(TWEET_FIELDS, addParams(params))
    }

    def withMediaFields(): Uri = {
      val params = List(
        MediaFields.DurationMs,
        MediaFields.Height,
        MediaFields.MediaKey,
        MediaFields.NonPublicMetrics,
        MediaFields.OrganicMetrics,
        MediaFields.PreviewImageUrl,
        MediaFields.PromotedMetrics,
        MediaFields.PublicMetrics,
        MediaFields.Type,
        MediaFields.Url,
        MediaFields.Width
      )

      uri.withQueryParam(MEDIA_FIELDS, addParams(params))
    }

    def withPlaceFields(): Uri = {
      val params = List(
        PlaceFields.ContainedWithin,
        PlaceFields.Country,
        PlaceFields.CountryCode,
        PlaceFields.FullName,
        PlaceFields.Geo,
        PlaceFields.Id,
        PlaceFields.Name,
        PlaceFields.PlaceType
      )

      uri.withQueryParam(PLACE_FIELDS, addParams(params))
    }

    def withPollFields(): Uri = {
      val params = List(
        PollFields.DurationMinutes,
        PollFields.EndDatetime,
        PollFields.Id,
        PollFields.Options,
        PollFields.VotingStatus
      )

      uri.withQueryParam("", addParams(params))
    }

    def withUserFields(): Uri = {
      val params = List(
        UserFields.CreatedAt,
        UserFields.Description,
        UserFields.Entities,
        UserFields.Id,
        UserFields.Location,
        UserFields.Name,
        UserFields.PinnedTweetId,
        UserFields.ProfileImageUrl,
        UserFields.Protected,
        UserFields.PublicMetrics,
        UserFields.Url,
        UserFields.Username,
        UserFields.Verified,
        UserFields.Withheld,
      )

      uri.withQueryParam("", addParams(params))
    }

    def withExpansions(): Uri = {
      val params = List(
        TweetExpansions.`Attachments.PollIds`,
        TweetExpansions.`Attachments.MediaKeys`,
        TweetExpansions.AuthorId,
        TweetExpansions.`Entities.Mentions.Username`,
        TweetExpansions.`Geo.PlaceId`,
        TweetExpansions.InReplyToUser,
        TweetExpansions.`ReferencedTweets.Id`,
        TweetExpansions.`ReferencedTweets.Id.AuthorId`
      )

      uri.withQueryParam("expansions", addParams(params))
    }

    private def addParams[A <: Enumeration](params: List[A]): String = {
      params.mkString(",")
    }
  }
}
