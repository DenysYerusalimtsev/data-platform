package com.prism.dataplatform.flink

import scala.concurrent.ExecutionContext

object Executors {

  val sameThread: ExecutionContext = new ExecutionContext with Serializable {
    override def execute(runnable: Runnable): Unit = runnable.run()
    override def reportFailure(cause: Throwable): Unit = ExecutionContext.defaultReporter.apply(cause)
  }

  implicit val sameThreadImp = sameThread
}
