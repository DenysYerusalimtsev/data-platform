package com.prism.dataplatform.twitter

import com.prism.dataplatform.core.{Application, Executor}
import com.prism.dataplatform.flink.Flink

object TwitterApplication extends Application[TweetsToMood] {
  override protected def executor: Executor[TweetsToMood] = Flink()
}
