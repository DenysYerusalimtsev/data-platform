package com.prism.dataplatform.core.config

import com.prism.dataplatform.core.Arguments

trait ArgumentsProvider {
  def argumentsFrom(args: Array[String]): Arguments
}