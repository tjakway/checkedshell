package com.jakway.checkshell.test.framework

import java.util.regex.Pattern

import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.test.framework.HasTestConfig

trait WithJobOutputMatcher extends HasTestConfig {
  def matchJobOutput(expected: FinishedProgramOutput): JobOutputMatcher =
    //unroll fields
    new JobOutputEqualityMatcher(
      Some(expected.exitCode),
      Some(expected.stdout),
      Some(expected.stderr),
      getTestConfig.futureTimeOut)

  def matchJobOutput(expectedExitCode: Option[Int],
                     expectedStdout: Option[String],
                     expectedStderr: Option[String]): JobOutputMatcher =
    new JobOutputEqualityMatcher(
      expectedExitCode,
      expectedStdout,
      expectedStderr,
      getTestConfig.futureTimeOut)

  def matchJobOutputRegex(expectedExitCode: Option[Pattern],
                          expectedStdoutRegex: Option[Pattern],
                          expectedStderrRegex: Option[Pattern]): JobOutputMatcher =
    new JobOutputRegexMatcher(
      expectedExitCode,
      expectedStdoutRegex,
      expectedStderrRegex,
      getTestConfig.futureTimeOut)

  def matchJobOutputRegex(expectedExitCode: Int,
                          expectedStdoutRegex: Pattern,
                          expectedStderrRegex: Pattern): JobOutputMatcher =
    matchJobOutputRegex(
      Some(Pattern.compile(expectedExitCode.toString)),
      Some(expectedStdoutRegex),
      Some(expectedStderrRegex))

}
