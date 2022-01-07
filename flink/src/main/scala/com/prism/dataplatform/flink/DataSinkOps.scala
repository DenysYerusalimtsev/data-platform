package com.prism.dataplatform.flink

import org.apache.flink.streaming.api.scala.DataStream

class DataSinkOps[A](sink: DataSink[A]) {

  def name(name: String): DataSink[A] = {
    (stream: DataStream[A]) => sink(stream).name(name)
  }

  def uname(name: String): DataSink[A] = {
    (stream: DataStream[A]) => sink(stream).name(name).uid(name)
  }

  def disableChaining(): DataSink[A] = {
    (stream: DataStream[A]) => sink(stream).disableChaining()
  }
}