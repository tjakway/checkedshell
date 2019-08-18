package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.error.{CheckedShellException, ErrorPrinter}

class CloseStreamError(override val msg: String)
  extends CheckedShellException(msg)
    with ErrorCause

class CloseStreamErrors(override val msg: String)
  extends CloseStreamError(msg)

object CloseStreamErrors {
  def apply(errs: Seq[Throwable]): CloseStreamErrors = {
    new CloseStreamErrors(new ErrorPrinter().printExceptions(errs))
  }
}


