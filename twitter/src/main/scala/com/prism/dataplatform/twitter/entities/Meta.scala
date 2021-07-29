package com.prism.dataplatform.twitter.entities

case class Meta(
                 newest_id: String,
                 oldest_id: String,
                 result_count: Int,
                 next_token: String
               )