package com.jakway.checkedshell.error.behavior

import com.jakway.checkedshell.error.{CheckedShellException, ErrorData}

object ThrowOnError extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit = {
    throw errorDataToException(errorData)
  }

  def errorDataToException(errorData: ErrorData): Exception = {
    new CheckedShellException(ErrorData.formatErrorData(errorData))
  }
}
