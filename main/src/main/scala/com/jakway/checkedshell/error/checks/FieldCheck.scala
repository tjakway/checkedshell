package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.error.cause.ErrorCause

trait FieldCheck[A] extends CheckFunction {
  protected def extractFromProgramOutput: FinishedProgramOutput => A

  protected def check(a: A): Option[FinishedProgramOutput => ErrorCause]

  override def apply(output: FinishedProgramOutput): Option[ErrorCause] =
    check(extractFromProgramOutput(output))
      .map(_.apply(output))
}
