package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.error.cause.ErrorCause

trait CheckFunction {
  def apply(output: ProgramOutput): Option[ErrorCause]
}
