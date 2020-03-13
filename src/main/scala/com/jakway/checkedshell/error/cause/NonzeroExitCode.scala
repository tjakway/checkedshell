package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.FinishedProgramOutput

case class NonzeroExitCode(override val output: FinishedProgramOutput)
  extends BadProgramOutput(output)
