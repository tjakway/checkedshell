package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.HasStreamWriters
import com.jakway.checkedshell.data.output.{FinishedProgramOutput, InProgressProgramOutput, ProgramOutput}
import com.jakway.checkedshell.error.ErrorData
import com.jakway.checkedshell.error.cause.ErrorCause
import com.jakway.checkedshell.error.checks.{CheckFunction, NonzeroExitCodeCheck, OutputCheckGroup}
import com.jakway.checkedshell.process.Job.{ErrorCheckFunctions, ExecJobF, JobInput, JobOutput, RunJobF}
import com.jakway.checkedshell.process.stream.RedirectionOperators
import com.jakway.checkedshell.process.stream.pipes.PipeManager
import com.jakway.checkedshell.process.stream.pipes.input.InputWrapper
import com.jakway.checkedshell.process.stream.pipes.output.OutputWrapper
import com.jakway.checkedshell.process.stream.pipes.output.OutputWrapper.{StderrWrapper, StdoutWrapper}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}

trait Job
  extends HasStreamWriters[Job]
    with RedirectionOperators[Job]
    with ShellOperators[Job]
    with ExtraShellOperators[Job] {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

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
                                 ec: ExecutionContext): JobOutput = {
    val execJobF = Job.runJobToExecJob(optDescription)(getRunJobF)
    execJobF(input)(rc)(ec)
  }


  private def composableExec(input: JobInput)
                            (implicit rc: RunConfiguration,
                                      ec: ExecutionContext): JobOutput = {

    if(rc.errorConfiguration.composeErrorChecks) {
      run(input)
    } else {
      execJob(input)
    }
  }

  private def getRunJobF: RunJobF =
    a => b => c => d => e => runJob(a)(b)(c)(d, e)

  private def getExecJobF: ExecJobF =
    a => b => c => run(a)(b, c)

  /**
   * This is what subclasses should override
   * A default implementation is provided for convenience and
   * use in logging
   * @param input
   * @param stdoutWrapper
   * @param stderrWrapper
   * @param rc
   * @param ec
   * @return
   */
  protected def runJob(input: JobInput)
                      (stdoutWrapper: StdoutWrapper)
                      (stderrWrapper: StderrWrapper)
                      (implicit rc: RunConfiguration,
                                ec: ExecutionContext): Future[Int] = {
    val f = rc.defaultJobBehavior.runDefaultJob(logger)
    f(input)(stdoutWrapper)(stderrWrapper)(rc)(ec)
  }

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

  protected def chain(pipeFunction: ProgramOutput => Option[ProgramOutput])
                     (to: Job): Job = {
    def newExecJob: ExecJobF =
      (input: JobInput) =>
      (rc: RunConfiguration) =>
      (ec: ExecutionContext) => {

        composableExec(input)(rc, ec)
          .flatMap(firstJobOutput =>
            to.run(pipeFunction(firstJobOutput))(rc, ec))(ec)
      }

    copyWithNewExecJob(newExecJob)
  }
  private def doPipe: ProgramOutput => Option[ProgramOutput] = Some.apply
  private def dontPipe: ProgramOutput => Option[ProgramOutput] = ignored => None
  private def branchPipe: Boolean => ProgramOutput => Option[ProgramOutput] = {
    branch =>
      if(branch) {
        doPipe
      } else {
        dontPipe
      }
  }

  def flatMap: Job => Job = chain(doPipe)
  //version of flatmap that ignores input from previous job
  override def sequence(arg: Job): Job = chain(dontPipe)(arg)

  override def and(snd: Job): Job = {
    branchCheckSuccess(
      snd.getExecJobF,
      FunctionJob.returnSuccessJob.getExecJobF,
      false)
  }

  override def andPipe(snd: Job): Job = {
    branchCheckSuccess(
      snd.getExecJobF,
      FunctionJob.returnSuccessJob.getExecJobF,
      true)
  }

  override def or(snd: Job): Job = {
    branchCheckSuccess(
      FunctionJob.returnSuccessJob.getExecJobF,
      snd.getExecJobF,
      false)
  }

  override def orPipe(snd: Job): Job = {
    branchCheckSuccess(
      FunctionJob.returnSuccessJob.getExecJobF,
      snd.getExecJobF,
      true)
  }

  private def branchCheckSuccess = branch(_ == 0)

  private def branch(decideCondition: Int => Boolean)
                    (whenTrue: ExecJobF,
                     whenFalse: ExecJobF,
                     pipe: Boolean): Job = {
    val newExecJob = (input: JobInput) =>
      (rc: RunConfiguration) =>
      (ex: ExecutionContext) => {
      implicit val ec: ExecutionContext = ex
      composableExec(input)(rc, ec)
        .flatMap(res => res.futureExitCode.map(e => (res, e)))
        .flatMap { args =>
          val (firstJobOutput, exitCode) = args

          def passArg: JobInput = {
            if(pipe) {
              Some(firstJobOutput)
            } else {
              None
            }
          }

          val branch = decideCondition(exitCode)
          if(branch) {
            whenTrue(passArg)(rc)(ec)
          } else {
            whenFalse(passArg)(rc)(ec)
          }
        }
    }

    copyWithNewExecJob(newExecJob)
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


  /**
   * sets up pipes for a runJob function and returns their readable ends
   * @param optDescription used for wrapper descriptions
   * @param runJobF
   * @return
   */
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

  private def mkStdoutPipe(encoding: String,
                   jobDescription: Option[String])
                  (implicit rc: RunConfiguration):
    (InputWrapper, OutputWrapper) = {

    val desc = jobDescription.map(d => s"stdout pipe of $d")

    PipeManager.newWrapperPair(encoding, desc)(rc.closeBehavior)
  }
  private def mkStderrPipe(encoding: String,
                   jobDescription: Option[String])
                  (implicit rc: RunConfiguration):
  (InputWrapper, OutputWrapper) = {
    val desc = jobDescription.map(d => s"stderr pipe of $d")
    PipeManager.newWrapperPair(encoding, desc)(rc.closeBehavior)
  }



  class OutputCheckFunctions(val outputCheckGroup: OutputCheckGroup) {
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
      }
    }

    def handleErrors(e: ErrorData,
                     runConfiguration: RunConfiguration): Unit = {
      runConfiguration.errorConfiguration.standardErrorBehavior.handleError(e)
    }
  }
}
