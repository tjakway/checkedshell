package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.FinishedProgramOutput

case class NonEmptyStderr(override val output: FinishedProgramOutput)
  extends NonEmptyStream(output, "stderr")
