package com.prism.dataplatform.azure.config

import scala.beans.BeanProperty

class AzureConfig {
  @BeanProperty var key: String = _
  @BeanProperty var endpoint: String = _
}