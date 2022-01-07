package com.prism.dataplatform.flink

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.source.{FromElementsFunction, SourceFunction}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import scala.concurrent.duration.Duration
import scala.collection.JavaConverters._

object Sources {

  def empty[A: TypeInformation](implicit env: StreamExecutionEnvironment): SourceFunction[A] = seq()

  def emptyInfinite[A: TypeInformation](implicit env: StreamExecutionEnvironment): SourceFunction[A] = {
    new SourceFunction[A] {
      @volatile var isRunning = true
      override def run(ctx: SourceFunction.SourceContext[A]): Unit = {
        while (isRunning) {
          Thread.sleep(100)
        }
      }
      override def cancel(): Unit = {
        isRunning = false
      }
    }
  }

  def iterable[A: TypeInformation](iterable: Iterable[A])(implicit env: StreamExecutionEnvironment): SourceFunction[A] =
    new FromElementsFunction[A](implicitly[TypeInformation[A]].createSerializer(env.getConfig), iterable.asJava)

  def seq[A: TypeInformation](el: A*)(implicit env: StreamExecutionEnvironment): SourceFunction[A] = iterable(el)

  def delayed[A: TypeInformation](timeout: Duration)(el: A*): SourceFunction[A] = delayedOf(timeout)(el.toIterable)

  def delayedOf[A: TypeInformation](timeout: Duration)(el: Iterable[A]): SourceFunction[A] = {
    new SourceFunction[A] {
      @volatile var isRunning = true
      override def run(ctx: SourceFunction.SourceContext[A]): Unit = {
        el.foreach(next => {
          if (isRunning) {
            ctx.collect(next)
            Thread.sleep(timeout.toMillis)
          }
        })
      }
      override def cancel(): Unit = {
        isRunning = false
      }
    }
  }

}
