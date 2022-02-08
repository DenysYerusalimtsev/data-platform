package com.prism.dataplatform.azure.config

import scala.beans.BeanProperty

class AzureProperties {
  @BeanProperty var consumerKey: String = _
  @BeanProperty var consumerSecret: String = _
  @BeanProperty var bearerToken: String = _
  @BeanProperty var token: String = _
  @BeanProperty var tokenSecret: String = _
}

case class AzureConfig(consumerKey: String,
                       consumerSecret: String,
                       bearerToken: String,
                       token: String,
                       tokenSecret: String)