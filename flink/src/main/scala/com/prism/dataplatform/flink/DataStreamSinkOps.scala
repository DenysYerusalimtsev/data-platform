package com.prism.dataplatform.flink

import org.apache.flink.streaming.api.datastream.DataStreamSink

class DataStreamSinkOps[A](sink: DataStreamSink[A]) {

  def uname(name: String): DataStreamSink[A] = {
    sink.name(name).uid(name)
  }
}
