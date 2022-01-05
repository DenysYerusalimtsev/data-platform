package com.prism.dataplatform.core

import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
abstract class BaseTest extends AnyFlatSpec
  with Matchers
