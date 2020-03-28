package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.FinishedProgramOutput

class NonEmptyStream(override val output: FinishedProgramOutput,
                     val streamName: String)
  extends BadProgramOutput(output) {

  override val description: String = s"Expected empty $streamName in < $output >"
}
