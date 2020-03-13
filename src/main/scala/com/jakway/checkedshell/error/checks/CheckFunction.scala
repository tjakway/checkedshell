package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.error.cause.ErrorCause

trait CheckFunction {
  def apply(output: FinishedProgramOutput): Option[ErrorCause]
}
