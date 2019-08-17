package com.jakway.checkedshell.error

import com.jakway.checkedshell.data.ProgramOutput

trait ErrorCause

case class UnhandledException(t: Throwable)
  extends ErrorCause

class BadProgramOutput(val output: ProgramOutput)
  extends ErrorCause

case class NonzeroExitCode(override val output: ProgramOutput)
  extends BadProgramOutput(output)
