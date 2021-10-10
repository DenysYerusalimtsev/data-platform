package com.prism.dataplatform.twitter.config

import com.prism.dataplatform.twitter.config.TwitterConfig

import scala.beans.BeanProperty

class Config {
  @BeanProperty var twitter: TwitterConfig = new TwitterConfig
}