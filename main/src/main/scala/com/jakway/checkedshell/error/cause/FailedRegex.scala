package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.output.FinishedProgramOutput

import scala.util.matching.Regex

case class FailedRegex(regex: Regex,
                       failedString: String,
                       override val output: FinishedProgramOutput)
  extends BadProgramOutput(output) {
  override val description: String = s"Expected output < $failedString >" +
    s" to match regex < ${regex.pattern.pattern()} > in output < $output > "
}

