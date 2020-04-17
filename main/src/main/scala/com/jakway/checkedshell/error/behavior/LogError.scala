package com.jakway.checkedshell.error.behavior

import java.util.Formatter

import com.jakway.checkedshell.error.ErrorData
import com.jakway.checkedshell.util.LogFunctions
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}

/**
 * @param logF error logging function
 */
class LogError(val logF: LogError.LogF = LogError.defaultLogF,
               val messageSeparator: String = LogError.defaultMessageSeparator)
  extends ErrorBehavior {
   val logger: Logger = LoggerFactory.getLogger(getClass)

   def formatErrorData(e: ErrorData): String = LogError.formatErrorData(e, messageSeparator)

   override def handleError(errorData: ErrorData): Unit = logF(logger)(formatErrorData(errorData))
 }

object LogError extends LogFunctions {
  def defaultLogF: LogF = error

  val defaultMessageSeparator: String = ": "

  def formatErrorData(e: ErrorData,
                      messageSeparator: String = defaultMessageSeparator): String = {

    val fmt = new Formatter()

    e.description.foreach { desc =>
      fmt.format("%s%s", desc, messageSeparator)
    }

    fmt.format("Error cause: %s", e.cause.toString)

    fmt.toString
  }

  def formatThrowable(t: Throwable): String = {
    t.toString
  }

  def logIfFailure[A](
    logger: Logger,
    logF: LogF = LogFunctions.error)
    (t: => Try[A]): Unit = {

    t match {
      case Success(_) => {}
      case Failure(e) => logF(logger)(formatThrowable(e))
    }
  }
}