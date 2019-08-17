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

    runJob(input)
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

  protected def runJob(input: Option[ProgramOutput])
                      (implicit ec: ExecutionContext): JobOutput

  protected def copyWithNewRunJob(newRunJob: Option[ProgramOutput] =>
                                             ExecutionContext => JobOutput): Job = {
    new MultiStepJob(newRunJob)
  }

  def checks: Set[CheckFunction] = Job.defaultCheckFunctions

  def map(f: ProgramOutput => ProgramOutput): Job = {
    def newRunJob(input: Option[ProgramOutput])
                 (implicit ec: ExecutionContext): JobOutput = {
      runJob(input).map(f)
    }

    //implicits only work for methods, see https://stackoverflow.com/questions/16414172/partially-applying-a-function-that-has-an-implicit-parameter
    def g: Option[ProgramOutput] => ExecutionContext => JobOutput =
      a => b => newRunJob(a)(b)
    copyWithNewRunJob(g)
  }

  //TODO: remove duplication between map and flatMap
  def flatMap(f: ProgramOutput => JobOutput): Job = {
    def newRunJob(input: Option[ProgramOutput])
                 (implicit ec: ExecutionContext): JobOutput = {
      runJob(input).flatMap(f)
    }

    //implicits only work for methods, see https://stackoverflow.com/questions/16414172/partially-applying-a-function-that-has-an-implicit-parameter
    def g: Option[ProgramOutput] => ExecutionContext => JobOutput =
      a => b => newRunJob(a)(b)
    copyWithNewRunJob(g)
  }
}

object Job {
  type JobOutput = Future[ProgramOutput]

  lazy val defaultCheckFunctions: Set[CheckFunction] = Set(NonzeroExitCodeCheck)
}
