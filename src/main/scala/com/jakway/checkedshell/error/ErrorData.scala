package com.jakway.checkedshell.error

import com.jakway.checkedshell.error.behavior.LogError
import com.jakway.checkedshell.error.cause.ErrorCause

case class ErrorData(description: Option[String], cause: ErrorCause)

object ErrorData {
  def formatErrorData(e: ErrorData,
                      messageSeparator: String = LogError.defaultMessageSeparator): String =
    LogError.formatErrorData(e, messageSeparator)
}
