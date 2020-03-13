package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.ProgramOutput

case class NonEmptyStderr(override val output: ProgramOutput)
  extends NonEmptyStream(output, "stderr")
