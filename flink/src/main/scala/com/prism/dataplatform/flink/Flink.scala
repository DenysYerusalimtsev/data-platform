package com.prism.dataplatform.flink

import com.prism.dataplatform.core.Executor
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

trait Flink extends Executor[FlinkJob[_]] {
  protected val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI()
  override def run(job: FlinkJob[_]): Unit = {
    job.setExecutionEnv(env)
    super.run(job)
    env.execute(job.name)
  }
}

object Flink {
  def apply(): Flink = new Flink {}
}