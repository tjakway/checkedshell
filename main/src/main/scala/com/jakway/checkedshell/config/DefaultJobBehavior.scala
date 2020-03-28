package com.jakway.checkedshell.config

import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.process.Job.{JobInput, RunJobF}
import com.jakway.checkedshell.process.stream.pipes.output.OutputWrapper.{StderrWrapper, StdoutWrapper}
import com.jakway.checkedshell.util.LogFunctions
import org.slf4j.Logger

import scala.concurrent.{ExecutionContext, Future}

trait DefaultJobBehavior {
  protected def logF: LogFunctions.LogF
  protected def noInputMsg: String = DefaultJobBehavior.defaultNoInputMsg

  protected def logNoInput: Logger => Unit = { (logger: Logger) =>
    logF(logger)(noInputMsg)
  }

  protected def logFinishedOutput: Logger => FinishedProgramOutput => Unit =
    (logger: Logger) => (finishedProgramOutput: FinishedProgramOutput) => {
      val msg = String.format("DefaultJobBehavior received input %s",
        finishedProgramOutput.toString)

      logF(logger)(msg)
    }

  def runDefaultJob(logger: Logger): RunJobF = {
    (input: JobInput) =>
    (stdoutWrapper: StdoutWrapper) =>
    (stderrWrapper: StderrWrapper) =>
    (rc: RunConfiguration) =>
    (ec: ExecutionContext) => {

      input match {
        case None => {
          logNoInput
          Future.successful(0)
        }
        case Some(foundInput) => {
          foundInput
            .toFuture(ec)
            .map { out =>
              logFinishedOutput(logger)
              out.exitCode
            }(ec)
        }
      }
    }
  }
}

object DefaultJobBehavior {
  val default: DefaultJobBehavior =
    new LoggingDefaultJob(LogFunctions.debug)

  val defaultNoInputMsg: String = "DefaultJobBehavior received no input"

  class LoggingDefaultJob(val logF: LogFunctions.LogF,
                          override val noInputMsg: String = defaultNoInputMsg)
    extends DefaultJobBehavior

  object DoNothingJob extends LoggingDefaultJob(LogFunctions.doNothing)
}
