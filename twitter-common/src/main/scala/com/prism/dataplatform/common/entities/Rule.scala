package com.prism.dataplatform.common.entities

case class Rule(
                 id: Option[String] = None,
                 value: Option[String],
                 tag: Option[String]
               )
