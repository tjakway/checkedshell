package com.jakway.checkedshell.error.behavior

trait ErrorBehavior {
  def handleError(errorData: ErrorData): Unit
}

object ErrorBehavior {
  case class ErrorData(description: Option[String], throwable: Option[Throwable])

  def apply(xs: Seq[ErrorBehavior]): ErrorBehavior = new CompositeErrorBehavior(xs)
}
