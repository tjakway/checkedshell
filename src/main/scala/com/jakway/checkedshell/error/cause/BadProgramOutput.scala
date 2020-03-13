package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.FinishedProgramOutput

class BadProgramOutput(val output: FinishedProgramOutput)
  extends ErrorCause
