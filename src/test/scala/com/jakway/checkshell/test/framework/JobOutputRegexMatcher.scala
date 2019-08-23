package com.jakway.checkshell.test.framework

import java.util.regex.Pattern

import scala.concurrent.duration.Duration

class JobOutputRegexMatcher(val expectedExitCode: Option[Pattern],
                            val expectedStdoutRegex: Option[Pattern],
                            val expectedStderrRegex: Option[Pattern],
                            override val timeout: Duration)
  extends JobOutputVerifier(timeout) {
  import JobOutputRegexMatcher._

  override protected def checkExitCode(exitCode: Int): Seq[String] =
    checkRegex(exitCode, expectedExitCode, "exitCode")
  override protected def checkStdout(stdout: String): Seq[String] =
    checkRegex(stdout, expectedStdoutRegex, "stdout")
  override protected def checkStderr(stderr: String): Seq[String] =
    checkRegex(stderr, expectedStderrRegex, "stderr")

}

object JobOutputRegexMatcher {
  //alternatively could just use the matchAllPattern in case of None
  //lazy val matchAllPattern: Pattern = Pattern.compile(""".*""")

  def errMsg[A](actual: A, pattern: Pattern, fieldName: String): String = {
    s"Expected $fieldName to match regex ${pattern.pattern()} " +
      s"but $actual does not match"
  }

  def checkRegex[A](actual: A, optPattern: Option[Pattern], fieldName: String): Seq[String] = {
    optPattern match {
      case Some(pattern) => {
        if(pattern.matcher(actual.toString).matches) {
          Seq()
        } else {
          Seq(errMsg(actual, pattern, fieldName))
        }
      }
      case None => Seq()
    }
  }
}

