package com.prism.dataplatform.flink

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

class SourceFunctionOps[A: TypeInformation](source: SourceFunction[A]) {
  def stream(implicit env: StreamExecutionEnvironment): DataStream[A] = {
    env.addSource(source)
  }
}
