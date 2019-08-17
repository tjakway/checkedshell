package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.ProgramOutput

case class NonEmptyStderr(override val output: ProgramOutput)
  extends BadProgramOutput(output) {
  override val description: String = s"Expected empty stderr in < $output >"
}
