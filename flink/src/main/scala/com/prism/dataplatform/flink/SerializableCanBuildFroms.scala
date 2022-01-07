package com.prism.dataplatform.flink

import scala.collection.generic.CanBuildFrom

// Remove when moving to scala 2.13
trait SerializableCanBuildFroms {
  implicit def listCanBuildFrom[T]: CanBuildFrom[List[T], T, List[T]] =
    new CanBuildFrom[List[T], T, List[T]] with Serializable {
      def apply(from: List[T]) = List.newBuilder[T]
      def apply() = List.newBuilder[T]
    }
}
