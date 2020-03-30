package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.FinishedProgramOutput

case class NonzeroExitCode(override val output: FinishedProgramOutput)
  extends UnexpectedExitCode(
    Some(NonzeroExitCode.checkDescription), output)

object NonzeroExitCode {
  val checkDescription: String = "x == 0"
}
