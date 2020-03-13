package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.output.ProgramOutput

trait StdoutCheck extends FieldCheck[String] {
  override protected def extractFromProgramOutput: ProgramOutput => String =
    _.stdout
}
