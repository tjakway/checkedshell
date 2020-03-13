package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.output.FinishedProgramOutput

trait StdoutCheck extends FieldCheck[String] {
  override protected def extractFromProgramOutput: FinishedProgramOutput => String =
    _.stdout
}
