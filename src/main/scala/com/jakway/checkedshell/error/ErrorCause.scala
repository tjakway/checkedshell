package com.jakway.checkedshell.error

import com.jakway.checkedshell.data.ProgramOutput

trait ErrorCause

object ErrorCause {
  /**
   * return or wrap the errors
   * @param xs
   * @return
   */
  def apply(xs: Set[ErrorCause]): ErrorCause = {
    if(xs.size == 1) {
      xs.head
    } else {
      MultipleErrors(xs)
    }
  }
}

case class MultipleErrors(errors: Set[ErrorCause])
  extends ErrorCause

case class UnhandledException(t: Throwable)
  extends ErrorCause

class BadProgramOutput(val output: ProgramOutput)
  extends ErrorCause

case class NonzeroExitCode(override val output: ProgramOutput)
  extends BadProgramOutput(output)
