package com.prism.dataplatform.flink

import com.prism.dataplatform.flink.StreamExecutionEnvironmentOps.TerminateJvmOnTaskCancellationSource
import com.prism.dataplatform.flink.syntax._
import grizzled.slf4j.Logging
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.runtime.execution.ExecutionState
import org.apache.flink.runtime.taskmanager.{RuntimeEnvironment, Task}
import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

import java.util.concurrent.CountDownLatch

class StreamExecutionEnvironmentOps(env: StreamExecutionEnvironment) {

  /**
   * Adds a parallel source with default parallelism that will shutdown any task manager it is
   * running on upon task cancellation.
   * <p>
   * <b>IMPORTANT:</b> In order for this to work properly, one must consider the following:
   * <ul>
   *   <li>Added source will shutdown only the task managers it is running on. If all task managers
   *       need to be shutdown then the user must make sure that this source is running on each
   *       available task manager. To achieve this, one can make sure that parallelism of this
   *       source is equal to the number of available task managers and the following configuration
   *       is set: {@code taskmanager.numberOfTaskSlots=1}.
   *   <li>When the restart strategy is selected and a task failure is detected, flink will try to
   *       recover by canceling and restarting existing tasks. However, by default, flink tries to
   *       find the smallest set of regions that must be restarted. In order to make sure that this
   *       source will be cancelled and restarted during failure recovery process, the following
   *       config option must be set: {@code jobmanager.execution.failover-strategy=full}.
   *   <li>Setting {@code cluster.intercept-user-system-exit=THROW} will prevent this source from
   *       shutting down the JVM. One must make sure that this config option is set to either
   *       {@code DISABLED} (default value) or {@code LOG}.
   * </ul>
   * <b>Note:</b> This config can be used to mitigate the {@code OutOfMemoryError metaspace}
   *    problem that we experience from time to time. It will not help with a memory leak itself,
   *    but at least we can restart task managers to free up the metaspace. Hopefully, we will fix
   *    the memory leak and remove this hack in the future.
   * <p>
   */
  def enableTaskManagerTerminationOnTaskFailure(enable: Boolean = false)
      : StreamExecutionEnvironment = {
    if (enable) {
      env.addSource(new TerminateJvmOnTaskCancellationSource()).name("JVM Termination")
        .ignore
    }

    env
  }
}

object StreamExecutionEnvironmentOps {

  class TerminateJvmOnTaskCancellationSource extends RichParallelSourceFunction[Unit] with Logging {

    @transient private var latch: CountDownLatch = _
    @transient private var containingTask: Task = _

    override def open(parameters: Configuration): Unit = {
      latch = new CountDownLatch(1)

      try {
        val envField = classOf[StreamingRuntimeContext].getDeclaredField("taskEnvironment")
        envField.setAccessible(true)
        val env = envField.get(getRuntimeContext).asInstanceOf[RuntimeEnvironment]

        val taskField = classOf[RuntimeEnvironment].getDeclaredField("containingTask")
        taskField.setAccessible(true)
        containingTask = taskField.get(env).asInstanceOf[Task]
      } catch {
        case e: Exception => {
          throw new RuntimeException("Failed to obtain the containing task", e)
        }
      }
    }

    override def run(sourceContext: SourceFunction.SourceContext[Unit]): Unit = {
      latch.await()
    }

    override def cancel(): Unit = {
      latch.countDown()
    }

    override def close(): Unit = {
      // When task is in CANCELING state it can indicate one of two things:
      // - job was cancelled
      // - a task failure is detected and flink tries to cancel all tasks from
      //   the same region(or joined regions) to either cancel a job or perform a restart
      if (containingTask.getExecutionState == ExecutionState.CANCELING) {
        warn("The task is being cancelled. Shutting down the JVM")

        System.exit(-1)
      }
    }
  }
}
