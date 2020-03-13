package com.jakway.checkedshell.error.checks
import com.jakway.checkedshell.data.output.ProgramOutput
import com.jakway.checkedshell.error.cause.{ErrorCause, NonzeroExitCode}

object NonzeroExitCodeCheck extends FieldCheck[Int] {
  override def apply(output: ProgramOutput): Option[ErrorCause] =
    if(output.exitCode == 0) {
      None
    } else {
      Some(NonzeroExitCode(output))
    }

  override protected def extractFromProgramOutput: ProgramOutput => Int = _.exitCode

  override protected def check(exitCode: Int): Option[ProgramOutput => ErrorCause] =
    if(exitCode == 0) {
      None
    } else {
      Some((output: ProgramOutput) => NonzeroExitCode(output))
    }
}
