package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.ProgramOutput

/**
 * Expected both streams to be empty
 * @param output
 */
case class NonQuietError(override val output: ProgramOutput)
  extends NonEmptyStream(output, "stdout and stderr")
