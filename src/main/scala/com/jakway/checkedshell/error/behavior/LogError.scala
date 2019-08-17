package com.jakway.checkedshell.error.behavior

import java.util.Formatter

import com.jakway.checkedshell.Util
import com.jakway.checkedshell.error.ErrorData
import org.slf4j.{Logger, LoggerFactory}

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

object LogError {
  type LogF = Logger => String => Unit

  def trace: LogF = l => m => l.trace(m)
  def debug: LogF = l => m => l.debug(m)
  def info: LogF = l => m => l.info(m)
  def warn: LogF = l => m => l.warn(m)
  def error: LogF = l => m => l.error(m)

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
}