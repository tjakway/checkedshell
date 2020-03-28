package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.FinishedProgramOutput

/**
 * Expected both streams to be empty
 *
 * @param output
 */
case class NonQuietError(override val output: FinishedProgramOutput)
  extends NonEmptyStream(output, "stdout and stderr")
