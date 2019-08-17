package com.jakway.checkedshell.error.behavior

import com.jakway.checkedshell.error.ErrorData

class CompositeErrorBehavior(xs: Seq[ErrorBehavior]) extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit =
    xs.foreach(_.handleError(errorData))
}
