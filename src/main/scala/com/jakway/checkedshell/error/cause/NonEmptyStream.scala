package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.ProgramOutput

class NonEmptyStream(override val output: ProgramOutput,
                     val streamName: String)
  extends BadProgramOutput(output) {

  override val description: String = s"Expected empty $streamName in < $output >"
}
