package com.prism.dataplatform.flink

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, WindowedStream}
import org.apache.flink.streaming.api.windowing.windows.Window

class WindowedStreamOps[T, K, W <: Window](val stream: WindowedStream[T, K, W]) {
  def foldLeft[A: TypeInformation](acc: A)(aggregator: (A, T) => A, merger: (A, A) => A): DataStream[A] = {
    Closures.clean(aggregator)
    Closures.clean(merger)
    stream.aggregate(new AggregateFunction[T, A, A] {
      override def createAccumulator() = acc
      override def add(value: T, accumulator: A) = aggregator(accumulator, value)
      override def getResult(accumulator: A) = accumulator
      override def merge(a: A, b: A) = merger(a, b)
    })
  }
}
