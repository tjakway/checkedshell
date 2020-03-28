package com.jakway.checkedshell.error.cause

case class MultipleErrors(errors: Set[ErrorCause])
  extends ErrorCause
