package com.prism.dataplatform.common.entities

case class Meta(
                 newest_id: Option[String],
                 oldest_id: Option[String],
                 result_count: Option[Int],
                 next_token: Option[String]
               )