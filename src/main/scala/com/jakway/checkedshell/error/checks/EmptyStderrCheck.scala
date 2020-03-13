package com.jakway.checkedshell.error.checks
import com.jakway.checkedshell.data.output.ProgramOutput
import com.jakway.checkedshell.error.cause.{ErrorCause, NonEmptyStderr}

object EmptyStderrCheck extends StderrCheck {
  override protected def check(a: String): Option[ProgramOutput => ErrorCause] =
    if(a.trim.isEmpty) {
      None
    } else {
      Some((output: ProgramOutput) => NonEmptyStderr(output))
    }
}
