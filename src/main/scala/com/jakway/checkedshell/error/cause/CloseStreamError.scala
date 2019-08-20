package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.error.{CheckedShellException, ErrorPrinter}

class CloseStreamError(override val msg: String)
  extends CheckedShellException(msg)
    with ErrorCause

object CloseStreamError {
  def wrapAsCloseStreamError(t: Throwable): CloseStreamError = {
    val errMsg = "Exception thrown while attempting to close stream"
    val e = new CloseStreamError(errMsg)
    e.initCause(t)
    e
  }
}

class CloseStreamErrors(override val msg: String)
  extends CloseStreamError(msg)

object CloseStreamErrors {
  def apply(errs: Seq[Throwable]): CloseStreamErrors = {
    new CloseStreamErrors(new ErrorPrinter().printExceptions(errs))
  }
}


