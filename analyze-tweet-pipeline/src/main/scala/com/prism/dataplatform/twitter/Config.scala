package com.prism.dataplatform.twitter

import com.prism.dataplatform.twitter.config.TwitterProperties
import com.prism.dataplatform.azure.config.AzureProperties

import scala.beans.BeanProperty

class Config {
  @BeanProperty var twitter: TwitterProperties = new TwitterProperties
  @BeanProperty var azure: AzureProperties = new AzureProperties
}