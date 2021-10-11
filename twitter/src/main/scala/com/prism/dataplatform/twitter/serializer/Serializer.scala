package com.prism.dataplatform.twitter.serializer

import cats.effect.IO
import com.google.gson.Gson

import scala.reflect.{ClassTag, classTag}

trait Serializer {
  val gson = new Gson

  def fromJson[A: ClassTag](json: String): IO[A] = {
    val classInfo = classTag[A].runtimeClass
    IO(gson.fromJson(json, classInfo))
  }
}
