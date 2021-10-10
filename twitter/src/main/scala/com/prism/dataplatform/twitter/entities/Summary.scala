package com.prism.dataplatform.twitter.entities

case class Summary(
                    deleted: Option[Int],
                    not_deleted: Option[Int],
                    created: Option[Int],
                    not_created: Option[Int],
                    valid: Option[Int],
                    invalid: Option[Int]
                  )