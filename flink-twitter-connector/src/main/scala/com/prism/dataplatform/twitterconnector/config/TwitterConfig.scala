package com.prism.dataplatform.twitterconnector.config

import scala.beans.BeanProperty

class TwitterConfig {
  @BeanProperty var consumerKey: String = _
  @BeanProperty var consumerSecret: String = _
  @BeanProperty var bearerToken: String = _
  @BeanProperty var token: String = _
  @BeanProperty var tokenSecret: String = _
}
