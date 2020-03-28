package com.jakway.checkedshell.config

import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.util.LogFunctions
import org.slf4j.Logger

trait DefaultJobBehavior {
  protected def logF: LogFunctions.LogF
  protected def noInputMsg: String = DefaultJobBehavior.defaultNoInputMsg

  def logNoInput: Logger => Unit = { (logger: Logger) =>
    logF(logger)(noInputMsg)
  }
  def logFinishedOutput: Logger => FinishedProgramOutput => Unit =
    (logger: Logger) => (finishedProgramOutput: FinishedProgramOutput) => {
      val msg = String.format("DefaultJobBehavior received input %s",
        finishedProgramOutput.toString)

      logF(logger)(msg)
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
