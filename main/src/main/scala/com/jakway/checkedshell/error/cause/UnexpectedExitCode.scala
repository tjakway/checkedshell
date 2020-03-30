package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.{FinishedProgramOutput, ProgramOutput}

class UnexpectedExitCode(val checkDescription: Option[String],
                         override val output: FinishedProgramOutput)
  extends BadProgramOutput(output) {

  override val description: String = {
    def printClass: String = {
      String.format("%s(%s, %s)", getClass.getName, checkDescription, output)
    }

    checkDescription match {
      case Some(found) => {
        String.format("Exit code %d failed check < %s > in %s",
          output.exitCode, found, output)
      }

      case None => printClass
    }
  }
}
