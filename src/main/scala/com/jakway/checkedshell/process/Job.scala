package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.error.ErrorData
import com.jakway.checkedshell.error.cause.ErrorCause
import com.jakway.checkedshell.error.checks.{CheckFunction, NonzeroExitCodeCheck}
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.{ExecutionContext, Future}

trait Job {
  final def run(input: Option[ProgramOutput])
               (implicit runConfiguration: RunConfiguration,
                         ec: ExecutionContext): JobOutput = {

    doRun(input)
      .map { output =>

        val errs = checks.foldLeft(Set.empty: Set[ErrorCause]) {
          case (acc, thisCheck) => {
            acc ++ thisCheck(output).toSet
          }
        }
        if(errs.isEmpty) {
          output
        } else {
          val finalCause = ErrorCause(errs)

          val errorData = ErrorData(None, finalCause)

          handleErrors(errorData, runConfiguration)

          //depending on the run configuration the error will either have caused execution to
          //terminate or will have been handled somehow
          //return the output if we're going to continue
          output
        }
      }
  }

  private def handleErrors(e: ErrorData, runConfiguration: RunConfiguration): Unit = {
    runConfiguration.errorBehavior.handleError(e)
  }

  protected def doRun(input: Option[ProgramOutput]): JobOutput

  def checks: Set[CheckFunction] = Job.defaultCheckFunctions
}

object Job {
  type JobOutput = Future[ProgramOutput]

  lazy val defaultCheckFunctions: Set[CheckFunction] = Set(NonzeroExitCodeCheck)
}
