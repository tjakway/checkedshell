package com.jakway.checkedshell.error

import java.util.Formatter

import com.jakway.checkedshell.Util
import com.jakway.checkedshell.error.ErrorBehavior.ErrorData
import org.slf4j.{Logger, LoggerFactory}

trait ErrorBehavior {
  def handleError(errorData: ErrorData): Unit
}

object ErrorBehavior {
  case class ErrorData(description: Option[String], throwable: Option[Throwable])

  def apply(xs: Seq[ErrorBehavior]): ErrorBehavior = new CompositeErrorBehavior(xs)
}
