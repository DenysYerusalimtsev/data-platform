package com.prism.dataplatform.flink

import com.prism.dataplatform.flink.batch.{BatchOperator, Batcher}
import com.prism.dataplatform.predef.{Either3, Either5}
import com.typesafe.scalalogging.Logger
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, _}
import org.apache.flink.util.Collector

import scala.annotation.unchecked.uncheckedVariance
import scala.collection.generic.CanBuildFrom
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.{higherKinds, implicitConversions}

class DataStreamOps[A: TypeInformation](val stream: DataStream[A]) {

  def via[B](func: DataFlow[A, B]): DataStream[B] = func(stream)

  def ~>[B](func: DataFlow[A, B]): DataStream[B] = func(stream)

  def ~>(func: DataSink[A]): DataStreamSink[A] = func(stream)

  def mapAsync[R: TypeInformation](atMost: Duration = 10.seconds, parallelism: Int = 16)(
    func: Function[A, Future[R]]): DataStream[R] =
    via(AsyncFunction.map(func).atMost(atMost).parallelism(parallelism)).name("mapAsync")

  def flatMapAsync[R: TypeInformation](atMost: Duration = 10.seconds, parallelism: Int = 16)(
    func: Function[A, Future[TraversableOnce[R]]]): DataStream[R] =
    via(AsyncFunction.flatMap(func).atMost(atMost).parallelism(parallelism)).name("flatMapAsync")

  def splitBy(predicate: A => Boolean): (DataStream[A], DataStream[A]) = {
    Closures.clean(predicate)
    splitWith {
      case value if predicate(value) => Left(value)
      case value => Right(value)
    }
  }

  def splitWith[L: TypeInformation, R: TypeInformation](
                                                         f: A => Either[L, R]): (DataStream[L], DataStream[R]) = {
    Closures.clean(f)
    val firstTag = new OutputTag[L]("first")
    val secondTag = new OutputTag[R]("second")
    // NOTE: do not convert to lambda - serialization will break
    val group = stream
      .process(new ProcessFunction[A, A] {
        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, A]#Context,
                                     out: Collector[A]) = {
          f(value) match {
            case Left(value) => ctx.output(firstTag, value)
            case Right(value) => ctx.output(secondTag, value)
          }
        }
      })
      .name("split2")
    (group.getSideOutput(firstTag), group.getSideOutput(secondTag))
  }

  def split3[
    O1: TypeInformation,
    O2: TypeInformation,
    O3: TypeInformation](f: A => Either3[O1, O2, O3]):
  (DataStream[O1], DataStream[O2], DataStream[O3]) = {
    Closures.clean(f)
    val tag1 = new OutputTag[O1]("out1")
    val tag2 = new OutputTag[O2]("out2")
    val tag3 = new OutputTag[O3]("out3")
    // NOTE: do not convert to lambda - serialization will break
    val group = stream
      .process(new ProcessFunction[A, A] {
        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, A]#Context,
                                     out: Collector[A]) = {
          f(value) match {
            case Either3.Out1(v) => ctx.output(tag1, v)
            case Either3.Out2(v) => ctx.output(tag2, v)
            case Either3.Out3(v) => ctx.output(tag3, v)
          }
        }
      })
      .name("split3")
    (
      group.getSideOutput(tag1),
      group.getSideOutput(tag2),
      group.getSideOutput(tag3))
  }

  def split5[
    O1: TypeInformation,
    O2: TypeInformation,
    O3: TypeInformation,
    O4: TypeInformation,
    O5: TypeInformation](f: A => Either5[O1, O2, O3, O4, O5])
  : (DataStream[O1], DataStream[O2], DataStream[O3], DataStream[O4], DataStream[O5]) = {
    Closures.clean(f)
    val tag1 = new OutputTag[O1]("out1")
    val tag2 = new OutputTag[O2]("out2")
    val tag3 = new OutputTag[O3]("out3")
    val tag4 = new OutputTag[O4]("out4")
    val tag5 = new OutputTag[O5]("out5")
    // NOTE: do not convert to lambda - serialization will break
    val group = stream
      .process(new ProcessFunction[A, A] {
        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, A]#Context,
                                     out: Collector[A]) = {
          val x: Either5[O1, O2, O3, O4, O5] = f(value)
          f(value) match {
            case Either5.Out1(v) => ctx.output(tag1, v)
            case Either5.Out2(v) => ctx.output(tag2, v)
            case Either5.Out3(v) => ctx.output(tag3, v)
            case Either5.Out4(v) => ctx.output(tag4, v)
            case Either5.Out5(v) => ctx.output(tag5, v)
          }
        }
      })
      .name("split5")
    (
      group.getSideOutput(tag1),
      group.getSideOutput(tag2),
      group.getSideOutput(tag3),
      group.getSideOutput(tag4),
      group.getSideOutput(tag5))
  }

  def fanOut[O1: TypeInformation, O2: TypeInformation](
                                                        f: A => (TraversableOnce[O1], TraversableOnce[O2])): (DataStream[O1], DataStream[O2]) = {
    Closures.clean(f)
    val tag1 = new OutputTag[O1]("out1")
    val tag2 = new OutputTag[O2]("out2")
    // NOTE: do not convert to lambda - serialization will break
    val group = stream
      .process(new ProcessFunction[A, A] {
        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, A]#Context,
                                     out: Collector[A]) = {
          val (out1, out2) = f(value)
          out1.foreach(value => ctx.output(tag1, value))
          out2.foreach(value => ctx.output(tag2, value))
        }
      })
      .name("fanOut2")
    (group.getSideOutput(tag1), group.getSideOutput(tag2))
  }

  def fanOut[O1: TypeInformation, O2: TypeInformation, O3: TypeInformation](
                                                                             f: A => (TraversableOnce[O1], TraversableOnce[O2], TraversableOnce[O3]))
  : (DataStream[O1], DataStream[O2], DataStream[O3]) = {
    Closures.clean(f)
    val tag1 = new OutputTag[O1]("out1")
    val tag2 = new OutputTag[O2]("out2")
    val tag3 = new OutputTag[O3]("out3")
    // NOTE: do not convert to lambda - serialization will break
    val group = stream
      .process(new ProcessFunction[A, A] {
        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, A]#Context,
                                     out: Collector[A]) = {
          val (out1, out2, out3) = f(value)
          out1.foreach(value => ctx.output(tag1, value))
          out2.foreach(value => ctx.output(tag2, value))
          out3.foreach(value => ctx.output(tag3, value))
        }
      })
      .name("fanOut3")
    (group.getSideOutput(tag1), group.getSideOutput(tag2), group.getSideOutput(tag3))
  }

  def fanOut[O1: TypeInformation, O2: TypeInformation, O3: TypeInformation, O4: TypeInformation](
                                                                                                  f: A => (TraversableOnce[O1], TraversableOnce[O2], TraversableOnce[O3], TraversableOnce[O4]))
  : (DataStream[O1], DataStream[O2], DataStream[O3], DataStream[O4]) = {
    Closures.clean(f)
    val tag1 = new OutputTag[O1]("out1")
    val tag2 = new OutputTag[O2]("out2")
    val tag3 = new OutputTag[O3]("out3")
    val tag4 = new OutputTag[O4]("out4")
    // NOTE: do not convert to lambda - serialization will break
    val group = stream
      .process(new ProcessFunction[A, A] {
        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, A]#Context,
                                     out: Collector[A]) = {
          val (out1, out2, out3, out4) = f(value)
          out1.foreach(value => ctx.output(tag1, value))
          out2.foreach(value => ctx.output(tag2, value))
          out3.foreach(value => ctx.output(tag3, value))
          out4.foreach(value => ctx.output(tag4, value))
        }
      })
      .name("fanOut4")
    (
      group.getSideOutput(tag1),
      group.getSideOutput(tag2),
      group.getSideOutput(tag3),
      group.getSideOutput(tag4))
  }

  def fanOut[
    O1: TypeInformation,
    O2: TypeInformation,
    O3: TypeInformation,
    O4: TypeInformation,
    O5: TypeInformation](
                          f: A => (
                            TraversableOnce[O1],
                              TraversableOnce[O2],
                              TraversableOnce[O3],
                              TraversableOnce[O4],
                              TraversableOnce[O5]))
  : (DataStream[O1], DataStream[O2], DataStream[O3], DataStream[O4], DataStream[O5]) = {
    Closures.clean(f)
    val tag1 = new OutputTag[O1]("out1")
    val tag2 = new OutputTag[O2]("out2")
    val tag3 = new OutputTag[O3]("out3")
    val tag4 = new OutputTag[O4]("out4")
    val tag5 = new OutputTag[O5]("out5")
    // NOTE: do not convert to lambda - serialization will break
    val group = stream
      .process(new ProcessFunction[A, A] {
        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, A]#Context,
                                     out: Collector[A]) = {
          val (out1, out2, out3, out4, out5) = f(value)
          out1.foreach(value => ctx.output(tag1, value))
          out2.foreach(value => ctx.output(tag2, value))
          out3.foreach(value => ctx.output(tag3, value))
          out4.foreach(value => ctx.output(tag4, value))
          out5.foreach(value => ctx.output(tag5, value))
        }
      })
      .name("fanOut5")
    (
      group.getSideOutput(tag1),
      group.getSideOutput(tag2),
      group.getSideOutput(tag3),
      group.getSideOutput(tag4),
      group.getSideOutput(tag5))
  }

  def merge[B: TypeInformation](other: DataStream[B]): DataStream[Either[A, B]] = {
    stream.connect(other).map(Left(_), Right(_)).name("merge")
  }

  def collect[B: TypeInformation](pf: PartialFunction[A, B]): DataStream[B] = {
    Closures.clean(pf)
    // NOTE: do not convert to lambda - serialization will break
    stream
      .process(new ProcessFunction[A, B] {
        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, B]#Context,
                                     out: Collector[B]) = {
          pf.lift(value) match {
            case Some(result) => out.collect(result)
            case None => // skip
          }
        }
      })
      .name("collect")
  }

  def peek(f: A => Unit): DataStream[A] = {
    Closures.clean(f)
    stream
      .process(new ProcessFunction[A, A] {
        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, A]#Context,
                                     out: Collector[A]) = {
          f(value)
          out.collect(value)
        }
      })
      .name("peek")
  }

  def throttle(timeout: Duration): DataStream[A] = {
    stream
      .process(new ProcessFunction[A, A] {
        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, A]#Context,
                                     out: Collector[A]) = {
          Thread.sleep(timeout.toMillis)
          out.collect(value)
        }
      })
      .name("throttle")
  }

  def logDebug(name: String, f: A => String): DataStream[A] = {
    Closures.clean(f)
    log(name, (el, logger) => logger.debug(f(el)))
  }

  def logDebug(f: A => String): DataStream[A] = {
    logDebug(nameOf(f.getClass), f)
  }

  def logInfo(name: String, f: A => String): DataStream[A] = {
    Closures.clean(f)
    log(name, (el, logger) => logger.info(f(el)))
  }

  def logInfo(f: A => String): DataStream[A] = {
    logInfo(nameOf(f.getClass), f)
  }

  def logWarn(name: String, f: A => String): DataStream[A] = {
    Closures.clean(f)
    log(name, (el, logger) => logger.warn(f(el)))
  }

  def logWarn(f: A => String): DataStream[A] = {
    logWarn(nameOf(f.getClass), f)
  }

  def logError(name: String, f: A => String): DataStream[A] = {
    Closures.clean(f)
    log(name, (el, logger) => logger.error(f(el)))
  }

  def logError(f: A => String): DataStream[A] = {
    logError(nameOf(f.getClass), f)
  }

  private def nameOf(clazz: Class[_]): String = {
    val pattern = "(.*)\\$\\$.*".r
    clazz.getName match {
      case pattern(name) => name
      case _ => clazz.getName
    }
  }

  private def log(name: String, f: (A, Logger) => Unit): DataStream[A] = {
    Closures.clean(f)
    stream
      .process(new ProcessFunction[A, A] {
        @transient var log: Logger = _

        override def open(parameters: Configuration): Unit = {
          log = Logger(name)
        }

        override def processElement(
                                     value: A,
                                     ctx: ProcessFunction[A, A]#Context,
                                     out: Collector[A]): Unit = {
          f(value, log)
          out.collect(value)
        }
      })
      .name("log")
  }

  def ignore: DataStreamSink[A] = stream.addSink(_ => ()).name("ignore")

  def narrowTo[B >: A : TypeInformation]: DataStream[B] = stream.map((a: A) => a.asInstanceOf[B])

  def uname(name: String) = stream.name(name).uid(name)

  /** Mini-batch the messages into the provided accumulator.
   *
   * @param maxSize     max size the batch might reach
   * @param maxDuration max duration the match might be gathered
   * @param seed        function to create a batch from the first element
   * @param aggregate   function to add the next element to the existing batch
   * @tparam B Batch type
   * @return Batched stream
   */
  def batch[B: TypeInformation](maxSize: Int, maxDuration: Duration, seed: A => B)(
    aggregate: (A, B) => B): DataStream[B] = {
    batchAcc(maxSize, maxDuration, seed)(aggregate)(identity)
  }

  /** Mini-batch the messages into the provided collection.
   *
   * @param maxSize     max size the batch might reach
   * @param maxDuration max duration the match might be gathered
   * @tparam Col Collection type
   * @return Batched stream
   */
  def batchTo[Col[_]](maxSize: Int, maxDuration: Duration = 60.seconds)(
    implicit cbf: CanBuildFrom[Nothing, A, Col[A@uncheckedVariance]],
    ti: TypeInformation[Col[A]]): DataStream[Col[A]] = {
    batchAcc(maxSize, maxDuration, seed = el => cbf() += el)((el, builder) => builder += el)(
      builder => builder.result())
  }

  /** Mini-batch the messages with the provided accumulator (advanced version).
   *
   * @param maxSize     max size the batch might reach
   * @param maxDuration max duration the match might be gathered
   * @param seed        function to create a batch from the first element
   * @param aggregate   function to add the next element to the existing batch
   * @param finish      function to finish the batch before emitting
   *                    (imagine we accumulate batch into a mutable array, but we want to emit the immutable copy)
   * @tparam B Intermediate batch type
   * @tparam F Final batch type
   * @return Batched stream
   */
  def batchAcc[B, F: TypeInformation](maxSize: Int, maxDuration: Duration, seed: A => B)(
    aggregate: (A, B) => B)(finish: B => F): DataStream[F] = {
    batchWith(maxSize, maxDuration)(Batcher(seed)(aggregate)(finish))
  }

  /** Mini-batch the messages with general purpose batcher which combines seed/accumulator/finish functions
   *
   * @param maxSize     max size the batch might reach
   * @param maxDuration max duration the match might be gathered
   * @param batcher     to batch the messages
   * @tparam B Intermediate batch type
   * @tparam F Final batch type
   * @return Batched stream
   */
  def batchWith[B, F: TypeInformation](maxSize: Int, maxDuration: Duration)(
    batcher: Batcher[A, B, F]): DataStream[F] = {
    stream.transform("Batch", new BatchOperator[A, B, F](batcher, maxSize, maxDuration))
  }
}

trait Output4 {}
