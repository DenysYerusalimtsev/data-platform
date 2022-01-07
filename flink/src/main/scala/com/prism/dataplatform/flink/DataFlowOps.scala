package com.prism.dataplatform.flink

import org.apache.flink.streaming.api.scala.DataStream

class DataFlowOps[A, B](flow: DataFlow[A, B]) {
  def name(name: String): DataFlow[A, B] = {
    (in: DataStream[A]) => {
      flow(in).name(name)
    }
  }
}
