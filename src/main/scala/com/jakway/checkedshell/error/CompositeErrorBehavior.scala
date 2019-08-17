package com.jakway.checkedshell.error

import com.jakway.checkedshell.error.ErrorBehavior.ErrorData

class CompositeErrorBehavior(xs: Seq[ErrorBehavior]) extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit =
    xs.foreach(_.handleError(errorData))
}
