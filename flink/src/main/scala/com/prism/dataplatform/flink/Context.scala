package com.prism.dataplatform.flink

import org.apache.flink.api.common.functions.{RichFunction, RuntimeContext}
import org.apache.flink.metrics.MetricGroup

class Context(runtime: Option[RuntimeContext]) {
  def group: Option[MetricGroup] = runtime.map(_.getMetricGroup)
}

object Context {
  def wrap(f: RichFunction): Context = {
    new Context(try {
      Some(f.getRuntimeContext)
    } catch {
      case _: IllegalStateException => None
    })
  }
}
