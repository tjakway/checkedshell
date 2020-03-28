package com.jakway.checkedshell.error.checks
import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.error.cause.{ErrorCause, NonzeroExitCode}

object NonzeroExitCodeCheck extends FieldCheck[Int] {
  override def apply(output: FinishedProgramOutput): Option[ErrorCause] =
    if(output.exitCode == 0) {
      None
    } else {
      Some(NonzeroExitCode(output))
    }

  override protected def extractFromProgramOutput: FinishedProgramOutput => Int = _.exitCode

  override protected def check(exitCode: Int): Option[FinishedProgramOutput => ErrorCause] =
    if(exitCode == 0) {
      None
    } else {
      Some((output: FinishedProgramOutput) => NonzeroExitCode(output))
    }
}
