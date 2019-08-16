package com.jakway.checkedshell.config

import java.util.Formatter

import com.jakway.checkedshell.Util
import com.jakway.checkedshell.config.ErrorBehavior.ErrorData
import org.slf4j.{Logger, LoggerFactory}

trait ErrorBehavior {
  def handleError(errorData: ErrorData): Unit
}

object ErrorBehavior {
  case class ErrorData(description: Option[String], throwable: Option[Throwable])

  def apply(xs: Seq[ErrorBehavior]): ErrorBehavior = new CompositeErrorBehavior(xs)
}

/**
 * @param logF error logging function
 */
class LogError(val logF: LogError.LogF = LogError.defaultLogF,
               val messageSeparator: String = ": ",
               val maxThrowableLines: Int = 15) extends ErrorBehavior {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  def formatErrorData(e: ErrorData): String = {
    val fmt = new Formatter()

    e match {
      case ErrorData(None, None) => fmt.format("Unknown error")
      case _ => {
        e.description.foreach { desc =>
          fmt.format(desc)
        }

        //add a separator if there's both a message and a throwable
        if(e.description.isDefined && e.throwable.isDefined) {
          fmt.format(messageSeparator)
        }

        e.throwable.foreach { t =>
          fmt.format(Util.throwableToString(t, maxThrowableLines))
        }
      }
    }

    fmt.toString
  }

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
}

object ThrowOnError extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit = {
    throw errorDataToException(errorData)
  }

  def errorDataToException(errorData: ErrorData): Exception = {
    val x = errorData.description match {
      case Some(desc) => new RuntimeException(desc)
      case None => new RuntimeException()
    }

    errorData.throwable match {
      case Some(t) => x.initCause(t)
      case None => {}
    }

    x
  }
}

object IgnoreError extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit = {}
}

class CompositeErrorBehavior(xs: Seq[ErrorBehavior]) extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit =
    xs.foreach(_.handleError(errorData))
}
