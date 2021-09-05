package com.prism.dataplatform.twitter

import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import matchers.should._
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
abstract class BaseTest extends AnyFlatSpec
  with Matchers
