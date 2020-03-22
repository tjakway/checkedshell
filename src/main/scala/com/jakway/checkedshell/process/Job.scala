package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.HasStreamWriters
import com.jakway.checkedshell.data.output.{FinishedProgramOutput, InProgressProgramOutput, ProgramOutput}
import com.jakway.checkedshell.error.ErrorData
import com.jakway.checkedshell.error.cause.ErrorCause
import com.jakway.checkedshell.error.checks.{CheckFunction, NonzeroExitCodeCheck}
import com.jakway.checkedshell.process.Job.{ErrorCheckFunctions, JobInput, JobOutput, RunJobF}
import com.jakway.checkedshell.process.stream.RedirectionOperators
import com.jakway.checkedshell.process.stream.pipes.PipeManager
import com.jakway.checkedshell.process.stream.pipes.input.InputWrapper
import com.jakway.checkedshell.process.stream.pipes.output.OutputWrapper
import com.jakway.checkedshell.process.stream.pipes.output.OutputWrapper.{StderrWrapper, StdoutWrapper}

import scala.concurrent.{ExecutionContext, Future}

trait Job
  extends HasStreamWriters[Job]
    with RedirectionOperators[Job] {

  def optDescription: Option[String]

  protected val errorCheckFunctions: ErrorCheckFunctions =
    new ErrorCheckFunctions(checks)

  final def run(input: JobInput)
               (implicit rc: RunConfiguration,
                         ec: ExecutionContext): JobOutput = {

    val (stdoutRead, stdoutWrite) =
      Job.mkStdoutPipe(rc.encoding, optDescription)

    val (stderrRead, stderrWrite) =
      Job.mkStderrPipe(rc.encoding, optDescription)


    val futureExitCode = runJob(input)(stdoutWrite)(stderrWrite)(rc, ec)
    val res = new InProgressProgramOutput(futureExitCode, stdoutRead, stderrRead)

    errorCheckFunctions
      .applyErrorChecks(res, rc, ec)
  }



  protected def runJob(input: JobInput)
                      (stdoutWrapper: StdoutWrapper)
                      (stderrWrapper: StderrWrapper)
                      (implicit rc: RunConfiguration,
                                ec: ExecutionContext): Future[Int]

  protected def copyWithNewRunJob(newRunJob: RunJobF): Job = {
    new MultiStepJob(newRunJob, getStreamWriters)
  }

  def checks: Set[CheckFunction] = Job.defaultCheckFunctions

  def map(f: FinishedProgramOutput => FinishedProgramOutput): Job = {
    def newRunJob(input: JobInput)
                 (implicit rc: RunConfiguration,
                           ec: ExecutionContext): JobOutput = {
      runJob(input).map(f)
    }

    //implicits only work for methods, see https://stackoverflow.com/questions/16414172/partially-applying-a-function-that-has-an-implicit-parameter
    def g: RunJobF =
      a =>
        (rc: RunConfiguration) =>
        (ec: ExecutionContext) =>
          newRunJob(a)(rc, ec)

    copyWithNewRunJob(g)
  }

  //TODO: remove duplication between map and flatMap
  //difficult because most of the code is signatures with little actual work
  def flatMap(f: ProgramOutput => JobOutput): Job = {
    def newRunJob(input: JobInput)
                 (implicit rc: RunConfiguration,
                           ec: ExecutionContext): JobOutput = {
      runJob(input).flatMap(f)
    }

    //implicits only work for methods, see https://stackoverflow.com/questions/16414172/partially-applying-a-function-that-has-an-implicit-parameter
    def g: RunJobF =
      a => (rc: RunConfiguration) => (ec: ExecutionContext) => newRunJob(a)(rc, ec)
    copyWithNewRunJob(g)
  }

  def flatMap(toOtherJob: Job): Job = {
    def newRunFunction: RunJobF =
      (input: Option[ProgramOutput]) =>
      (rc: RunConfiguration) =>
      (ec: ExecutionContext) => {
        //run our job first, then the passed job
        val firstJob: Job = this
        val secondJob: Job = toOtherJob
        firstJob.runJob(input)(rc, ec)
          .flatMap { firstJobRes =>
            secondJob.runJob(Some(firstJobRes))(rc, ec)
          }(ec)
      }

     copyWithNewRunJob(newRunFunction)
  }
}

object Job {
  type JobInput = Option[ProgramOutput]
  type JobOutput = Future[ProgramOutput]
  type RunJobF = JobInput =>
                  StdoutWrapper =>
                  StderrWrapper =>
                  RunConfiguration =>
                  ExecutionContext =>
                  Future[Int]

  lazy val defaultCheckFunctions: Set[CheckFunction] = Set(NonzeroExitCodeCheck)


  def mkStdoutPipe(encoding: String,
                   jobDescription: Option[String])
                  (implicit rc: RunConfiguration):
    (InputWrapper, OutputWrapper) = {

    val desc = jobDescription.map(d => s"stdout pipe of $d")

    PipeManager.newWrapperPair(encoding, desc)(rc.closeBehavior)
  }
  def mkStderrPipe(encoding: String,
                   jobDescription: Option[String])
                  (implicit rc: RunConfiguration):
  (InputWrapper, OutputWrapper) = {
    val desc = jobDescription.map(d => s"stderr pipe of $d")
    PipeManager.newWrapperPair(encoding, desc)(rc.closeBehavior)
  }



  class ErrorCheckFunctions(val checks: Set[CheckFunction]) {
    def applyErrorChecks(output: ProgramOutput,
              runConfiguration: RunConfiguration,
              ec: ExecutionContext): JobOutput = {
      def transformProgramOutput(
          programOutput: ProgramOutput): ProgramOutput = {
        applyErrorChecks(programOutput, runConfiguration)
        programOutput
      }
      def id: Throwable => Throwable = x => x

      //TODO: integrate job description parameter
      val recoverF: Throwable => ProgramOutput = { (throwable: Throwable) =>
        val ret = runConfiguration
          .errorConfiguration
          .handleFailedFuture
          .handleError(None, throwable)

        new FinishedProgramOutput(ret, new String(), new String())
      }

      //PartialFunction.apply is deprecated
      val recoverPF: PartialFunction[Throwable, ProgramOutput] = {
        case x => recoverF(x)
      }

      output
        .transform(transformProgramOutput, id)(ec)
        .recover(recoverPF)(ec)
    }

    def applyErrorChecks(programOutput: ProgramOutput,
                         runConfiguration: RunConfiguration): Unit = {
      //TODO: implement error checks on pipes
      /*
      val errs: Set[ErrorCause] = checks.foldLeft(Set.empty: Set[ErrorCause]) {
        case (acc, thisCheck) => {
          acc ++ thisCheck(output).toSet
        }
      }
       */
      val errs: Set[ErrorCause] = Set.empty
      if(errs.nonEmpty) {
        val finalCause = ErrorCause(errs)

        val errorData = ErrorData(None, finalCause)

        handleErrors(errorData, runConfiguration)

        //depending on the run configuration the error will either have caused execution to
        //terminate or will have been handled somehow
        //return the output if we're going to continue
      }
    }

    def handleErrors(e: ErrorData,
                     runConfiguration: RunConfiguration): Unit = {
      runConfiguration.errorConfiguration.standardErrorBehavior.handleError(e)
    }
  }
}
