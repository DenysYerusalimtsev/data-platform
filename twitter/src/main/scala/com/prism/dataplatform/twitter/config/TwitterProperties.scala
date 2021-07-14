package com.prism.dataplatform.twitter.config

import com.prism.dataplatform.twitter.config.Constants._
import org.apache.flink.api.common.typeinfo.TypeInformation

import java.util.Properties

trait TwitterProperties {
  implicit val evidence: TypeInformation[String] = TypeInformation.of(classOf[String])

  def setupProperties(config: TwitterConfig): Properties = {
    val properties = new Properties
    properties.setProperty(CONSUMER_KEY, config.consumerKey)
    properties.setProperty(CONSUMER_SECRET, config.consumerSecret)
    properties.setProperty(TOKEN, config.token)
    properties.setProperty(TOKEN_SECRET, config.tokenSecret)

    properties
  }
}
