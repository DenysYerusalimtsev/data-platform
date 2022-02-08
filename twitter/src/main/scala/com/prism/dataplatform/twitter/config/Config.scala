package com.prism.dataplatform.twitter.config

import scala.beans.BeanProperty

class Config {
  @BeanProperty var twitter: TwitterProperties = new TwitterProperties
  @BeanProperty var azure: TwitterProperties = new TwitterProperties
}