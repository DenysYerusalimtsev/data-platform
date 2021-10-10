package com.prism.dataplatform.twitter

import com.prism.dataplatform.core.{Application, Executor}
import com.prism.dataplatform.flink.Flink

object TwitterApplication extends Application[AnalyzeTweets] {
  override protected def executor: Executor[AnalyzeTweets] = Flink()
}
