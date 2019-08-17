package com.jakway.checkedshell.error

import com.jakway.checkedshell.error.cause.ErrorCause

case class ErrorData(description: Option[String], cause: ErrorCause)
