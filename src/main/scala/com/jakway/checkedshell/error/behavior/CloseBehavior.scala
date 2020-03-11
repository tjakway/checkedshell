package com.jakway.checkedshell.error.behavior

import java.io.Closeable

import com.jakway.checkedshell.error.CheckedShellException
import com.jakway.checkedshell.error.behavior.CloseBehavior.CloseReturnType
import com.jakway.checkedshell.error.cause.CloseStreamError

class CloseBehavior(val logError: LogError)
                   (val allowDoubleClose: Boolean,
                    val throwOnError: Boolean,
                    val alwaysReturnSuccess: Boolean,
                    val logErrors: Boolean) {
  def throwIfIndicated(ret: => CloseReturnType): CloseReturnType = {
    ret match {
      case Left(e) if throwOnError => throw e
      case _ => ret
    }
  }

  def doOnSuccess(ret: => CloseReturnType, f: () => ()): CloseReturnType = {
    ret match {
      case Left(_) => ret
      case Right(_) => {
        f()
        ret
      }
    }
  }
}

object CloseBehavior {
  type CloseReturnType = Either[CheckedShellException, Unit]

  val defaultLogError: LogError = new LogError(LogError.error)
  def default(logError: LogError = defaultLogError): CloseBehavior = {
    new CloseBehavior(logError)(
      allowDoubleClose = false,
      throwOnError = false,
      alwaysReturnSuccess = false,
      logErrors = true
    )
  }

  def wrapCloseCall(c: => Closeable): CloseReturnType = {
    try {
      c.close()
      Right({})
    } catch {
      case t: Throwable => Left(CloseStreamError.wrapAsCloseStreamError(t))
    }
  }
}
