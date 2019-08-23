package com.jakway.checkshell.test.framework

import java.util.Formatter

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.test.framework.HasTestConfig
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

//TODO: eliminate duplication with GetProgramOutput
abstract class JobOutputVerifier(val timeout: Duration)
  extends Matcher[JobOutput] {

  //return Seq of error messages on failure
  protected def checkExitCode(exitCode: Int): Seq[String]
  protected def checkStdout(stdout: String): Seq[String]
  protected def checkStderr(stderr: String): Seq[String]

  protected def getProgramOutput(jobOutput: JobOutput): ProgramOutput =
    Await.result(jobOutput, timeout)

  def apply(left: JobOutput): MatchResult = {
    val actualOutput: ProgramOutput = getProgramOutput(left)

    //see if all checks passed
    val checkResults =
      checkExitCode(actualOutput.exitCode) ++
      checkStdout(actualOutput.stdout) ++
      checkStderr(actualOutput.stderr)

    val success = checkResults.isEmpty

    //format failed checks into an error message
    lazy val errMsg: String = {
      val fmt = new Formatter()
      fmt.format("%s failed checks:", actualOutput)
      checkResults.foreach { thisErrorMessage =>
        fmt.format("\t%s%n", thisErrorMessage)
      }
      fmt.toString
    }

    MatchResult(success,
      errMsg,
      s"$actualOutput passed checks")
  }
}

class JobOutputEqualityMatcher(val expectedExitCode: Option[Int],
                               val expectedStdout: Option[String],
                               val expectedStderr: Option[String],
                               override val timeout: Duration)
  extends JobOutputVerifier(timeout) {

  private def errMsg[A](actual: A, expected: A, fieldName: String): String = {
    s"Expected $fieldName to be equal to $expected but got $actual"
  }

  private def check[A](actual: A, optExpected: Option[A], fieldName: String): Seq[String] = {
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

  override protected def checkExitCode(exitCode: Int): Seq[String] =
    check(exitCode, expectedExitCode, "exitCode")

  override protected def checkStdout(stdout: String): Seq[String] =
    check(stdout, expectedStdout, "stdout")

  override protected def checkStderr(stderr: String): Seq[String] =
    check(stderr, expectedStderr, "stderr")
}

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

