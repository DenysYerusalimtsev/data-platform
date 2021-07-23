package com.prism.dataplatform.twitter.entities

final case class Error(
                        detail: String,
                        field: Option[String],
                        parameter: String,
                        resource_id: String,
                        resource_type: String,
                        section: Option[String],
                        title: String,
                        `type`: Option[String],
                        value: Option[String]
                      )
