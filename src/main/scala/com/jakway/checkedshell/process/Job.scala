package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.HasStreamWriters
import com.jakway.checkedshell.data.output.{FinishedProgramOutput, InProgressProgramOutput, ProgramOutput}
import com.jakway.checkedshell.error.ErrorData
import com.jakway.checkedshell.error.cause.ErrorCause
import com.jakway.checkedshell.error.checks.{CheckFunction, NonzeroExitCodeCheck}
import com.jakway.checkedshell.process.Job._
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
    errorCheckFunctions(execJob(input)(rc, ec))
  }

  protected def execJob(input: JobInput)
                       (implicit rc: RunConfiguration,
                                 ec: ExecutionContext): Future[ProgramOutput] = {
    val (stdoutRead, stdoutWrite) =
      Job.mkStdoutPipe(rc.encoding, optDescription)

    val (stderrRead, stderrWrite) =
      Job.mkStderrPipe(rc.encoding, optDescription)


    val futureExitCode = runJob(input)(stdoutWrite)(stderrWrite)(rc, ec)
    Future.successful(
      new InProgressProgramOutput(futureExitCode, stdoutRead, stderrRead))
  }

  private def getRunJobF: RunJobF =
    a => b => c => d => e => runJob(a)(b)(c)(d, e)

  private def getExecJobF: ExecJobF =
    a => b => c => execJob(a)(b, c)

  protected def runJob(input: JobInput)
                      (stdoutWrapper: StdoutWrapper)
                      (stderrWrapper: StderrWrapper)
                      (implicit rc: RunConfiguration,
                                ec: ExecutionContext): Future[Int]

  protected def copyWithNewExecJob(
    newExecJob: ExecJobF): Job = {
    new MultiStepJob(newExecJob, getStreamWriters)
  }

  def checks: Set[CheckFunction] = Job.defaultCheckFunctions

  def map(f: ProgramOutput => ProgramOutput): Job = {
    def newExecJob: ExecJobF =
      (input: JobInput) =>
      (rc: RunConfiguration) =>
      (ec: ExecutionContext) => {
        execJob(input)(rc, ec).map(f)(ec)
      }

    copyWithNewExecJob(newExecJob)
  }

  def mapFinishedOutput(
    f: FinishedProgramOutput => FinishedProgramOutput): Job = {
    def newExecJob: ExecJobF =
      (input: JobInput) =>
        (rc: RunConfiguration) =>
          (ex: ExecutionContext) => {

            implicit val ec: ExecutionContext = ex
            execJob(input)(rc, ec)
              .flatMap { output =>
                output.toFuture.map { x =>
                  val res = f(x)
                  res: ProgramOutput
                }
              }
          }

    copyWithNewExecJob(newExecJob)
  }

  def flatMap(f: ProgramOutput => Future[ProgramOutput]): Job = {
    ???
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

  type ExecJobF = JobInput =>
    RunConfiguration =>
    ExecutionContext =>
    Future[ProgramOutput]

  lazy val defaultCheckFunctions: Set[CheckFunction] = Set(NonzeroExitCodeCheck)


  def runJobToExecJob(optDescription: Option[String])
                     (runJobF: RunJobF): ExecJobF = {
    (input: JobInput) => (rc: RunConfiguration) => (ec: ExecutionContext) => {

      val (stdoutRead, stdoutWrite) =
        Job.mkStdoutPipe(rc.encoding, optDescription)(rc)

      val (stderrRead, stderrWrite) =
        Job.mkStderrPipe(rc.encoding, optDescription)(rc)


      val futureExitCode = runJobF(input)(stdoutWrite)(stderrWrite)(rc)(ec)
      Future.successful(
        new InProgressProgramOutput(futureExitCode, stdoutRead, stderrRead))
    }
  }

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
    def apply(in: JobOutput)
              (implicit rc: RunConfiguration,
                        ec: ExecutionContext): JobOutput = {
      def transformProgramOutput(
          programOutput: ProgramOutput): ProgramOutput = {
        doErrorChecks(programOutput, rc)
        programOutput
      }
      def id: Throwable => Throwable = x => x

      //TODO: integrate job description parameter
      val recoverF: Throwable => ProgramOutput = { (throwable: Throwable) =>
        val ret = rc
          .errorConfiguration
          .handleFailedFuture
          .handleError(None, throwable)

        new FinishedProgramOutput(ret, new String(), new String())
      }

      //PartialFunction.apply is deprecated
      val recoverPF: PartialFunction[Throwable, ProgramOutput] = {
        case x => recoverF(x)
      }

      in.transform(transformProgramOutput, id)(ec)
        .recover(recoverPF)(ec)
    }

    private def doErrorChecks(
                      programOutput: ProgramOutput,
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
