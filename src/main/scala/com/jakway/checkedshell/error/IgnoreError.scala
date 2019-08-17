package com.jakway.checkedshell.error

import com.jakway.checkedshell.error.ErrorBehavior.ErrorData

object IgnoreError extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit = {}
}
