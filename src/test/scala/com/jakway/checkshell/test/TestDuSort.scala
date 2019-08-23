package com.jakway.checkshell.test

import com.jakway.checkedshell.process.{Job, Process}
import com.jakway.checkedshell.test.framework.HasDefaultTestConfig
import com.jakway.checkedshell.util.SearchPath
import com.jakway.checkshell.test.framework.WithJobOutputMatcher
import org.scalatest.Matchers
import org.scalatest.flatspec.AnyFlatSpecLike

class TestDuSort
  extends AnyFlatSpecLike
    with Matchers
    with WithJobOutputMatcher
    with HasDefaultTestConfig {
  import TestDuSort._

  //without du and sort installed the rest of the tests will obviously fail
  testName should "have programs on path" in {
    val res = for {
      _ <- SearchPath.searchPathEither("du")
      _ <- SearchPath.searchPathEither("sort")
    } yield {}

    res should be ('right)
  }

  testName should "run without crashing" in {
    jobToTest.run(None) should matchJobOutput(Some(0), None, None)
  }
}

object TestDuSort {
  val testName: String ="Sorted du job"

  val jobToTest: Job = {
    Process("du", Seq("-h"))
      .flatMap(Process("sort", Seq("-h")))
  }
}
