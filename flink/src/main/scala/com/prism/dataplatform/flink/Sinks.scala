package com.prism.dataplatform.flink

import org.apache.flink.streaming.api.functions.sink.SinkFunction

object Sinks {

  def ignore[A]: SinkFunction[A] = {
    new SinkFunction[A] {
      override def invoke(value: A, context: SinkFunction.Context): Unit = {}
    }
  }
}
