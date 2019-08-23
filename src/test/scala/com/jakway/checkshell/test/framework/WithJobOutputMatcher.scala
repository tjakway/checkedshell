package com.jakway.checkshell.test.framework

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.test.framework.HasTestConfig

trait WithJobOutputMatcher extends HasTestConfig {
  def matchJobOutput(expected: ProgramOutput): JobOutputEqualityMatcher =
    //unroll fields
    new JobOutputEqualityMatcher(
      Some(expected.exitCode),
      Some(expected.stdout),
      Some(expected.stderr),
      getTestConfig.futureTimeOut)

  def matchJobOutput(expectedExitCode: Option[Int],
                     expectedStdout: Option[String],
                     expectedStderr: Option[String]): JobOutputEqualityMatcher =
    new JobOutputEqualityMatcher(
      expectedExitCode,
      expectedStdout,
      expectedStderr,
      getTestConfig.futureTimeOut)
}
