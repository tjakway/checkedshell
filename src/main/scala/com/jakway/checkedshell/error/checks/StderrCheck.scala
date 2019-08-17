package com.jakway.checkedshell.error.checks
import com.jakway.checkedshell.data.ProgramOutput

trait StderrCheck extends FieldCheck[String] {
  override protected def extractFromProgramOutput: ProgramOutput => String =
    _.stderr
}
