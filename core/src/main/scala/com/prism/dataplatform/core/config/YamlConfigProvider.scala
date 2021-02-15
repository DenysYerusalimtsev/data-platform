package com.prism.dataplatform.core.config

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import java.io.{File, FileInputStream}
import scala.reflect.{ClassTag, classTag}

trait ConfigFile {
  def configFile(path: String): FileInputStream =
    new FileInputStream(new File(path))
}

private[core] trait YamlConfigProvider extends ConfigProvider with ConfigFile {
  override def configFrom[C: ClassTag](path: String): C = {
    val classInfo = classTag[C].runtimeClass

    if (path.isEmpty) classInfo.newInstance.asInstanceOf[C]
    else {
      val input = configFile(path)
      val yaml = new Yaml(new Constructor(classInfo))
      yaml.load(input).asInstanceOf[C]
    }
  }
}
