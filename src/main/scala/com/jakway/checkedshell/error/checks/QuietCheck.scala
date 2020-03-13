package com.jakway.checkedshell.error.checks
import com.jakway.checkedshell.data.output.ProgramOutput
import com.jakway.checkedshell.error.cause.{ErrorCause, NonQuietError}

/**
 * expect both streams to be empty
 */
object QuietCheck extends StdoutStderrCheck {
  override protected def check(a: (String, String)): Option[ProgramOutput => ErrorCause] = {
    def failed(s: String): Boolean = s.trim.isEmpty

    if(failed(a._1) || failed(a._2)) {
      Some(NonQuietError.apply)
    } else {
      None
    }
  }
}
