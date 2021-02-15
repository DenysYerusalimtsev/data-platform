package com.prism.dataplatform.spark

import com.prism.dataplatform.core.ConfiguredJob
import org.apache.spark.sql.SparkSession

import scala.reflect.ClassTag

abstract class SparkJob[C: ClassTag] extends ConfiguredJob[C] {
  private var _spark: SparkSession = _

  implicit lazy val spark: SparkSession = _spark

  private[spark] def setSparkSession(spark: SparkSession): Unit = {
    _spark = spark
  }
}