package com.prism.dataplatform

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, WindowedStream}
import org.apache.flink.streaming.api.windowing.windows.Window

import scala.language.implicitConversions

package object flink {

  type DataFlow[A, B] = DataStream[A] => DataStream[B]
  type IDataFlow[A] = DataStream[A] => DataStream[A]
  type DataSink[A] = DataStream[A] => DataStreamSink[A]
  type IProcessFunction[A] = ProcessFunction[A, A]

  trait FlinkSyntax extends SerializableCanBuildFroms {

    implicit def toDataStreamOps[T: TypeInformation](stream: DataStream[T]): DataStreamOps[T] =
      new DataStreamOps[T](stream)

    implicit def toWindowedStreamOps[T, K, W <: Window](stream: WindowedStream[T, K, W]): WindowedStreamOps[T, K, W] =
      new WindowedStreamOps[T, K, W](stream)

    implicit def toSourceFunctionOps[A: TypeInformation](source: SourceFunction[A]): SourceFunctionOps[A] =
      new SourceFunctionOps[A](source)

    implicit def toDataFlowOps[A, B](flow: DataFlow[A, B]): DataFlowOps[A, B] =
      new DataFlowOps[A, B](flow)

    implicit def sinkFunctionDataSink[A](sink: SinkFunction[A]): DataSink[A] =
      (stream: DataStream[A]) => stream.addSink(sink)

    implicit def toDataSinkOps[A](sink: DataSink[A]): DataSinkOps[A] =
      new DataSinkOps[A](sink)

    implicit def sinkFunctionOps[A](sink: SinkFunction[A]): SinkFunctionOps[A] =
      new SinkFunctionOps[A](sink)

    implicit def dataStreamSinkOps[A](sink: DataStreamSink[A]): DataStreamSinkOps[A] =
      new DataStreamSinkOps[A](sink)

    implicit def streamExecutionEnvironmentOps(env: StreamExecutionEnvironment): StreamExecutionEnvironmentOps =
      new StreamExecutionEnvironmentOps(env)
  }

  object syntax extends FlinkSyntax
}
