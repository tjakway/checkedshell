package com.jakway.checkedshell.error.behavior

import com.jakway.checkedshell.error.ErrorData

object IgnoreError extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit = {}
}
