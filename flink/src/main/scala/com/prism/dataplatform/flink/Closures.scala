package com.prism.dataplatform.flink

import org.apache.flink.api.common.ExecutionConfig.ClosureCleanerLevel
import org.apache.flink.api.scala.ClosureCleaner

object Closures {
  def clean[A <: AnyRef](closure: A,
                         checkSerializable: Boolean = true,
                         cleanLevel: ClosureCleanerLevel = ClosureCleanerLevel.RECURSIVE): A = {
    ClosureCleaner.clean(closure, checkSerializable, cleanLevel)
    closure
  }
}
