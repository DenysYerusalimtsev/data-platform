package com.prism.dataplatform.flink

import com.prism.dataplatform.flink.syntax.toDataSinkOps
import com.prism.dataplatform.flink.syntax.sinkFunctionDataSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction

class SinkFunctionOps[A](sink: SinkFunction[A]) {

  def name(name: String): DataSink[A] = {
    val dataSink: DataSink[A] = sink
    dataSink.name(name)
  }

  def uname(name: String): DataSink[A] = {
    val dataSink: DataSink[A] = sink
    dataSink.uname(name)
  }

  def disableChaining(): DataSink[A] = {
    val dataSink: DataSink[A] = sink
    dataSink.disableChaining()
  }
}
