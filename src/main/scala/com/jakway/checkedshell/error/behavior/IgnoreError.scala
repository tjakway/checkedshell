package com.jakway.checkedshell.error.behavior

object IgnoreError extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit = {}
}
