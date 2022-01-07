package com.prism.dataplatform.flink

import java.util.concurrent.TimeUnit
import com.prism.dataplatform.flink.Executors.sameThread
import com.prism.dataplatform.flink.Closures.clean
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.async.{ResultFuture, RichAsyncFunction}
import org.apache.flink.streaming.api.scala.{AsyncDataStream, DataStream}

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.{Failure, Success}

case class AsyncFunctionBuilder[Ctx](
                                      private val _setup: Context => Ctx,
                                      private val _stop: Ctx => Unit,
                                      private val _options: AsyncFunction.Options = AsyncFunction.Options()) {
  def stop(f: Ctx => Unit) = copy(_stop = f)

  def atMost(duration: Duration) = copy(_options = _options.copy(timeout = duration))

  def parallelism(threads: Int) = copy(_options = _options.copy(parallelism = threads))

  def map[A, B: TypeInformation](f: (Ctx, A) => Future[B]): AsyncFunction[Ctx, A, B] =
    AsyncFunction(_setup, _stop, (ctx, next) => f(ctx, next).map(Iterable(_))(sameThread))

  def flatMap[A, B: TypeInformation](
                                      f: (Ctx, A) => Future[TraversableOnce[B]]): AsyncFunction[Ctx, A, B] =
    AsyncFunction(_setup, _stop, f)
}

object AsyncFunctionBuilder {
  def stateless: AsyncFunctionBuilder[Unit] = AsyncFunctionBuilder(_ => (), _ => ())
}

case class AsyncFunction[Ctx, A, B: TypeInformation](
                                                      private val _setup: Context => Ctx,
                                                      private val _stop: Ctx => Unit,
                                                      private val _via: (Ctx, A) => Future[TraversableOnce[B]],
                                                      private val _options: AsyncFunction.Options)
  extends Function[DataStream[A], DataStream[B]] {
  def atMost(duration: Duration) = copy(_options = _options.copy(timeout = duration))

  def parallelism(threads: Int) = copy(_options = _options.copy(parallelism = threads))

  def apply(stream: DataStream[A]): DataStream[B] = {
    val func: RichAsyncFunction[A, B] = new RichAsyncFunction[A, B] {
      @transient var ctx: Ctx = _

      override def open(parameters: Configuration): Unit = ctx = {
        val runtime =
          try {
            Some(getRuntimeContext)
          } catch {
            case _: IllegalStateException => None
          }
        _setup(new Context(runtime))
      }

      override def close(): Unit = {
        if (ctx != null)
          _stop(ctx)
      }

      override def asyncInvoke(el: A, resultFuture: ResultFuture[B]): Unit = {
        _via(ctx, el).onComplete {
          case Success(results) => resultFuture.complete(results.toIterable)
          case Failure(e) => resultFuture.completeExceptionally(e)
        }(sameThread)
      }
    }
    AsyncDataStream.unorderedWait(
      stream,
      func,
      _options.timeout.toMillis,
      TimeUnit.MILLISECONDS,
      _options.parallelism)
  }
}

object AsyncFunction {

  case class Options(parallelism: Int = 16, timeout: Duration = 60.seconds)

  def apply[Ctx, A, B: TypeInformation](
                                         _setup: Context => Ctx,
                                         _stop: Ctx => Unit,
                                         _via: (Ctx, A) => Future[TraversableOnce[B]],
                                         _options: Options = Options()): AsyncFunction[Ctx, A, B] =
    new AsyncFunction(clean(_setup), clean(_stop), clean(_via), _options)

  def setup[Ctx](f: Context => Ctx) = AsyncFunctionBuilder[Ctx](f, (_: Ctx) => ())

  def map[A, B: TypeInformation](f: A => Future[B]): AsyncFunction[Unit, A, B] =
    AsyncFunctionBuilder.stateless.map((_, next) => f(next))

  def flatMap[A, B: TypeInformation](
                                      f: A => Future[TraversableOnce[B]]): AsyncFunction[Unit, A, B] =
    AsyncFunctionBuilder.stateless.flatMap((_, next) => f(next))
}
