package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.ProgramOutput

case class NonEmptyStderr(override val output: ProgramOutput)
  extends NonEmptyStream(output, "stderr")
