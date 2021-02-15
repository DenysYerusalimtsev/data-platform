package com.prism.dataplatform.spark

import com.prism.dataplatform.core.Executor
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession


trait Spark extends Executor[SparkJob[_]] {
  private var _name: String = _

  protected lazy val spark: SparkSession = {
    val conf = new SparkConf()
      .setIfMissing("spark.master", "local[*]")

    SparkSession.builder()
      .appName(_name)
      .config(conf)
      .getOrCreate()
  }

  override def run(job: SparkJob[_]): Unit = {
    _name = job.name
    job.setSparkSession(spark)
    super.run(job)
  }
}

object Spark {
  def apply(): Spark = new Spark {}
}