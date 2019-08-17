package com.jakway.checkedshell.error.behavior
import com.jakway.checkedshell.error.ErrorData

object ExitOnError extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit = System.exit(defaultErrorStatus)

  val defaultErrorStatus: Int = 1
}
