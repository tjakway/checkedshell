package com.jakway.checkedshell.config

import java.util.Formatter

import com.jakway.checkedshell.config.ErrorConfiguration.HandleFailedFuture
import com.jakway.checkedshell.error.ErrorData
import com.jakway.checkedshell.error.behavior.{ErrorBehavior, ThrowOnError}
import com.jakway.checkedshell.error.cause.UnhandledException
import com.jakway.checkedshell.util.LogFunctions
import org.slf4j.{Logger, LoggerFactory}

case class ErrorConfiguration(standardErrorBehavior: ErrorBehavior,
                              handleFailedFuture: HandleFailedFuture)

object ErrorConfiguration {
  val default: ErrorConfiguration = ErrorConfiguration(
    ThrowOnError,
    HandleFailedFuture.default
  )

  sealed trait HandleFailedFuture {
    def handleError(jobDesc: Option[String],
                    throwable: Throwable): Int

    protected def throwableToErrorData(jobDesc: Option[String],
                                       throwable: Throwable): ErrorData = {
      val fmt: Formatter = {
        val sb = new StringBuffer()
        new Formatter(sb)
      }

      fmt.format("Future returned failure")
      jobDesc.foreach(desc =>
        fmt.format(" for job < %s >", desc))

      ErrorData(Some(fmt.toString),
        HandleFailedFuture.FutureFailed(throwable))
    }
  }

  object HandleFailedFuture {
    /**
     * throw on error by default
     */
    val default: HandleFailedFuture =
      new HandleFailedFutureWithErrorBehavior(ThrowOnError)
    val defaultExitCode: Int = -1

    protected case class FutureFailed(throwable: Throwable)
      extends UnhandledException(throwable)

    class HandleFailedFutureWithErrorBehavior(errorBehavior: ErrorBehavior)
      extends HandleFailedFuture {

      def handleError(jobDesc: Option[String],
                      throwable: Throwable): Int = {
        errorBehavior.handleError(throwableToErrorData(jobDesc, throwable))
        HandleFailedFuture.defaultExitCode
      }
    }
    case class ExitCodeOnFailedFuture(
                                       throwableToExitCode: Throwable => Int,
                                       logErrorData: LogFunctions.LogF = LogFunctions.doNothing)
      extends HandleFailedFuture {
      val logger: Logger = LoggerFactory.getLogger(getClass)

      def handleError(jobDesc: Option[String],
                      throwable: Throwable): Int = {
        lazy val errorData = throwableToErrorData(jobDesc, throwable)
        val res = throwableToExitCode(throwable)

        logErrorData(logger)(
          String.format(
            "Converted failed Future to exit code %d, error data: %s",
            res, ErrorData.formatErrorData(errorData)))

        res
      }
    }
  }
}
