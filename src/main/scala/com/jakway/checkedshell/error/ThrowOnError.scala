package com.jakway.checkedshell.error

import com.jakway.checkedshell.error.ErrorBehavior.ErrorData

object ThrowOnError extends ErrorBehavior {
  override def handleError(errorData: ErrorData): Unit = {
    throw errorDataToException(errorData)
  }

  def errorDataToException(errorData: ErrorData): Exception = {
    val x = errorData.description match {
      case Some(desc) => new RuntimeException(desc)
      case None => new RuntimeException()
    }

    errorData.throwable match {
      case Some(t) => x.initCause(t)
      case None => {}
    }

    x
  }
}
