package com.prism.dataplatform.azure.config

import scala.beans.BeanProperty

class AzureProperties {
  @BeanProperty var key: String = _
  @BeanProperty var endpoint: String = _
}

case class AzureConfig(key: String, endpoint: String)