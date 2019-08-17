package com.jakway.checkedshell.error.cause

import com.jakway.checkedshell.data.ProgramOutput

import scala.util.matching.Regex

case class FailedRegex(regex: Regex,
                       failedString: String,
                       override val output: ProgramOutput)
  extends BadProgramOutput(output) {
  override val description: String = s"Expected output < $failedString >" +
    s" to match regex < ${regex.pattern.pattern()} > in output < $output > "
}

