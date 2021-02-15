package com.prism.dataplatform.flink

trait Flink extends Executor[FlinkJob[_]] {
  protected val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

  override def run(job: FlinkJob[_]): Unit = {
    job.setExecutionEnv(env)
    super.run(job)
    env.execute(job.name)
  }
}

object Flink {
  def apply(): Flink = new Flink {}
}
© 2021 GitHub, Inc.