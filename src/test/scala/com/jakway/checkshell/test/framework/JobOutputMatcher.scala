package com.jakway.checkshell.test.framework

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.test.framework.HasTestConfig
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class JobOutputMatcher(val expectedOutput: ProgramOutput,
                       val timeout: Duration)
  extends Matcher[JobOutput] {

  def apply(left: JobOutput): MatchResult = {
    val actualOutput: ProgramOutput = Await.result(left, timeout)
    val success = actualOutput == expectedOutput
    MatchResult(success,
      s"$actualOutput did not match $expectedOutput",
      s"$actualOutput matched $expectedOutput")
  }
}

trait WithJobOutputMatcher extends HasTestConfig {
  def matchJobOutput(expected: ProgramOutput): JobOutputMatcher =
    new JobOutputMatcher(expected, getTestConfig.futureTimeOut)
}

