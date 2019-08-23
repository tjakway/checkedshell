package com.jakway.checkshell.test

import com.jakway.checkedshell.process.{Job, Process}
import com.jakway.checkedshell.test.framework.HasDefaultTestConfig
import com.jakway.checkedshell.util.SearchPath
import com.jakway.checkshell.test.framework.{GetProgramOutput, WithJobOutputMatcher}
import org.scalatest.Matchers
import org.scalatest.flatspec.AnyFlatSpecLike

class TestDuSort
  extends AnyFlatSpecLike
    with Matchers
    with WithJobOutputMatcher
    with GetProgramOutput
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

  it should "run without crashing" in {
    jobToTest.run(None) should matchJobOutput(Some(0), None, None)
  }

  it should "have identical output across runs" in {
    val firstRunRes = getProgramOutput(jobToTest.run(None))
    jobToTest.run(None) should matchJobOutput(firstRunRes)
  }

  it should "have non-empty stdout" in {
    import com.jakway.checkshell.test.framework.JobOutputRegexMatcher.CommonPatterns._
    jobToTest.run(None) should matchJobOutputRegex(
      0,
      matchNonWhitespace,
      matchWhitespaceOrEmpty)
  }
}

object TestDuSort {
  val testName: String ="Sorted du job"

  /**
   * need to recreate the job for each test
   * or the writers will be reused
   * @return
   */
  def jobToTest: Job = {
    Process("du", Seq("-h", "-d", "1"))
      .flatMap(Process("sort", Seq("-h")))
  }
}
