package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.ProgramOutput

case class NonzeroExitCode(override val output: ProgramOutput)
  extends BadProgramOutput(output)
