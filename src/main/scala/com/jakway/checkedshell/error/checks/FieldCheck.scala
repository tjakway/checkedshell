package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.error.cause.ErrorCause

trait FieldCheck[A] extends CheckFunction {
  protected def extractFromProgramOutput: ProgramOutput => A

  protected def check(a: A): Option[ProgramOutput => ErrorCause]

  override def apply(output: ProgramOutput): Option[ErrorCause] =
    check(extractFromProgramOutput(output))
      .map(_.apply(output))
}
