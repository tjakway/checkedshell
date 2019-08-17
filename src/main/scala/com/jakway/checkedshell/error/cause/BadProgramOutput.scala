package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.ProgramOutput

class BadProgramOutput(val output: ProgramOutput)
  extends ErrorCause
