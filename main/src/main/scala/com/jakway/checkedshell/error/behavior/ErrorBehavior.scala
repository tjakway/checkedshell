package com.jakway.checkedshell.error.behavior

import com.jakway.checkedshell.error.ErrorData

trait ErrorBehavior {
  def handleError(errorData: ErrorData): Unit
}

object ErrorBehavior {
  def apply(xs: Seq[ErrorBehavior]): ErrorBehavior = new CompositeErrorBehavior(xs)
}
