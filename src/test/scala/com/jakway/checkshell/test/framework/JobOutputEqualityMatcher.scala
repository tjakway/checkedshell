package com.jakway.checkshell.test.framework

import scala.concurrent.duration.Duration

class JobOutputEqualityMatcher(val expectedExitCode: Option[Int],
                               val expectedStdout: Option[String],
                               val expectedStderr: Option[String],
                               override val timeout: Duration)
  extends JobOutputVerifier(timeout) {
  import JobOutputEqualityMatcher._

  override protected def checkExitCode(exitCode: Int): Seq[String] =
    checkEquality(exitCode, expectedExitCode, "exitCode")

  override protected def checkStdout(stdout: String): Seq[String] =
    checkEquality(stdout, expectedStdout, "stdout")

  override protected def checkStderr(stderr: String): Seq[String] =
    checkEquality(stderr, expectedStderr, "stderr")
}

object JobOutputEqualityMatcher {
  private def errMsg[A](actual: A, expected: A, fieldName: String): String = {
    s"Expected $fieldName to be equal to $expected but got $actual"
  }

  def checkEquality[A](actual: A, optExpected: Option[A], fieldName: String): Seq[String] = {
    optExpected match {
      case Some(expected) => {
        if(actual != expected) {
          Seq(errMsg(actual, expected, fieldName))
        } else {
          Seq()
        }
      }
      //no check if the expected variable isn't defined
      case None => Seq()
    }
  }
}