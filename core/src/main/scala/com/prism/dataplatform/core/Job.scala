package com.prism.dataplatform.core

import com.prism.dataplatform.core.config.YamlConfigProvider
import com.typesafe.scalalogging.LazyLogging

import scala.reflect.ClassTag

trait Job extends LazyLogging {
  def name: String = this.getClass.getSimpleName
    .replace("Job", "")

  def script(): Unit
}

abstract class ConfiguredJob[C: ClassTag] extends Job
  with YamlConfigProvider {
  private var _args: Arguments = _
  protected lazy val arguments: Arguments = _args

  protected lazy val config: C = configFrom(arguments.configPath)

  private[core] def setArguments(args: Arguments): Unit = {
    _args = args
  }
}