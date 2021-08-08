package com.prism.dataplatform.twitter.entities

final case class Error(
                        detail: Option[String],
                        details: Option[Seq[String]],
                        field: Option[String],
                        parameter: Option[String],
                        resource_id: Option[String],
                        resource_type: Option[String],
                        section: Option[String],
                        title: Option[String],
                        `type`: Option[String],
                        value: Option[String]
                      )
