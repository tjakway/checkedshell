package com.jakway.checkedshell.error.cause

case class UnhandledException(t: Throwable)
  extends ErrorCause
