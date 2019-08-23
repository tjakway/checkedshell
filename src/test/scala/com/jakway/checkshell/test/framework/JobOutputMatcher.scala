package com.jakway.checkshell.test.framework

import java.util.Formatter

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.test.framework.HasTestConfig
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

//TODO: eliminate duplication with GetProgramOutput
abstract class JobOutputMatcher(val timeout: Duration)
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





