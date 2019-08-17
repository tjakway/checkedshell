package com.jakway.checkedshell.error.checks
import com.jakway.checkedshell.data.ProgramOutput

abstract class StdoutStderrCheck extends FieldCheck[(String, String)] {
  override protected def extractFromProgramOutput: ProgramOutput => (String, String) = {
    output => (output.stdout, output.stderr)
  }
}
