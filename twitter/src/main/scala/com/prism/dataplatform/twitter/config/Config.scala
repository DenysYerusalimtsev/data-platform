package com.prism.dataplatform.twitter.config

import scala.beans.BeanProperty

class Config {
  @BeanProperty var twitter: TwitterConfig = new TwitterConfig
}