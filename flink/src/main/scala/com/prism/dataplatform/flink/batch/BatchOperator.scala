package com.prism.dataplatform.flink.batch

import com.prism.dataplatform.flink.Closures
import grizzled.slf4j.Logging
import org.apache.flink.streaming.api.operators.{AbstractStreamOperator, OneInputStreamOperator}
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.runtime.streamrecord.{LatencyMarker, StreamRecord}

import java.util.concurrent.ScheduledFuture
import scala.concurrent.duration.Duration

/** The operator which performs mini-batching of the data.
  * It is possible to specify batch max size and max batch duration.
  *
  * The batch will be emitted when:
  * - either max size or max duration is triggered.
  * - before a checkpoint
  * - before operator closes
  *
  * NOTES:
  * - when the batch is emitted the largest timestamp from all seen messages is used
  * - watermarks and latency markers are delayed until the batch is emitted
  * - the operator has no state
  *
  * @param batcher to batch the messages
  * @param maxSize max size the batch might reach
  * @param maxDuration max duration the match might be gathered
  * @tparam E Stream element type
  * @tparam B Intermediate batch type
  * @tparam F Final batch type
  */
class BatchOperator[E, B, F](batcher: Batcher[E, B, F], maxSize: Int, maxDuration: Duration)
    extends AbstractStreamOperator[F]
    with OneInputStreamOperator[E, F] with Logging {

  @transient private var batchOpt: Option[BatchRecord[B]] = _
  @transient private var totalBatches: Long = _
  @transient private var watermarkOpt: Option[Watermark] = _
  @transient private var latencyMarkerOpt: Option[LatencyMarker] = _

  override def open() = {
    batchOpt = None
    watermarkOpt = None
    latencyMarkerOpt = None
  }

  override def processElement(element: StreamRecord[E]) = {
    val nextBatch = batchOpt
      .map { batch =>
        batch.modify(batcher.aggregate(element.getValue, _), element.getTimestamp)
      }
      .getOrElse {
        val currentBatch = totalBatches
        val timer = getProcessingTimeService.registerTimer(
          getProcessingTimeService.getCurrentProcessingTime + maxDuration.toMillis,
          _ => {
            debug(s"Batch max duration $maxDuration is exceeded, triggering flush")
            if (currentBatch == totalBatches) flush(cancelTimer = false)
          })
        new BatchRecord(batcher.seed(element.getValue), 1, element.getTimestamp, timer)
      }
    batchOpt = Some(nextBatch)
    if (nextBatch.size >= maxSize) {
      debug(s"Batch max size $maxSize is reached, triggering flush")
      flush()
    }
  }

  override def processWatermark(mark: Watermark) = {
    watermarkOpt = Some(mark)
  }

  override def processLatencyMarker(latencyMarker: LatencyMarker) = {
    latencyMarkerOpt = Some(latencyMarker)
  }

  override def prepareSnapshotPreBarrier(checkpointId: Long) = {
    debug(s"Checkpoint $checkpointId is about to start, triggering flush")
    flush()
  }

  def flush(cancelTimer: Boolean = true): Unit = {
    batchOpt.foreach { batch =>
      val completed = batch.complete(batcher.finish)
      debug(s"Flushing the batch $completed")
      output.collect(completed)
      if (cancelTimer)
        batch.timer.cancel(true)
    }
    batchOpt = None
    watermarkOpt.foreach(super.processWatermark)
    watermarkOpt = None
    latencyMarkerOpt.foreach(super.processLatencyMarker)
    latencyMarkerOpt = None
    totalBatches = totalBatches + 1
  }

  override def close() = {
    debug(s"The operator is closing, triggering flush")
    flush()
  }
}

/** Batcher which has to provide
  * - seed function to create a batch from the first element
  * - aggregate function to add the next element to the existing batch
  * - finish function to finish the batch before emitting
  *               (imagine we accumulate batch into a mutable array, but we want to emit the immutable copy)
  * @tparam E Element type
  * @tparam B Intermediate batch type
  * @tparam F Final batch type
  */
trait Batcher[E, B, F] extends Serializable {
  def seed(el: E): B
  def aggregate(el: E, batch: B): B
  def finish(batch: B): F
}

object Batcher {
  def apply[E, B, F](seedWith: E => B)(aggregateWith: (E, B) => B)(
      finishWith: B => F): Batcher[E, B, F] = {
    val seedWithCleaned = Closures.clean(seedWith)
    val aggregateWithCleaned = Closures.clean(aggregateWith)
    val finishWithCleaned = Closures.clean(finishWith)
    new Batcher[E, B, F] {
      override def seed(el: E) = seedWithCleaned(el)
      override def aggregate(el: E, batch: B) = aggregateWithCleaned(el, batch)
      override def finish(batch: B) = finishWithCleaned(batch)
    }
  }
}

class BatchRecord[B](
    val value: B,
    val size: Long,
    val timestamp: Long,
    val timer: ScheduledFuture[_]) {

  def modify(via: B => B, nextTimestamp: Long): BatchRecord[B] = {
    new BatchRecord(via(value), size + 1, math.max(timestamp, nextTimestamp), timer)
  }

  def complete[F](finish: B => F): StreamRecord[F] =
    new StreamRecord[F](finish(value), timestamp)

  override def toString = s"BatchRecord(value=$value,size=$size,timestamp=$timestamp)"
}
