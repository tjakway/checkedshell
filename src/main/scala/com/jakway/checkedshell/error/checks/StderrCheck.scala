package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.output.FinishedProgramOutput

trait StderrCheck extends FieldCheck[String] {
  override protected def extractFromProgramOutput: FinishedProgramOutput => String =
    _.stderr
}
