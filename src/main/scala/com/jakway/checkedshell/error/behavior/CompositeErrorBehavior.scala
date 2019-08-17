package com.jakway.checkedshell.error.behavior

class CompositeErrorBehavior(xs: Seq[ErrorBehavior]) extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit =
    xs.foreach(_.handleError(errorData))
}
