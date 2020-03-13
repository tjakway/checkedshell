package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.output.FinishedProgramOutput

abstract class StdoutStderrCheck extends FieldCheck[(String, String)] {
  override protected def extractFromProgramOutput: FinishedProgramOutput => (String, String) = {
    output => (output.stdout, output.stderr)
  }
}
