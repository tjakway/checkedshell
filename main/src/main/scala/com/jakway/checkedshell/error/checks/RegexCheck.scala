package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.output.ProgramOutput
import com.jakway.checkedshell.error.cause.{ErrorCause, FailedRegex}
import com.jakway.checkedshell.error.checks.PerLineCheck.GetLines

import scala.util.matching.Regex

class RegexCheck(val getLines: GetLines,
                 val regex: Regex,
                 override val stopEarly: Boolean) extends PerLineCheck {
  override protected def checkLine: ProgramOutput =>
    String => Option[ErrorCause] = output => line => {
    if(regex.pattern.matcher(line).matches) {
      None
    } else {
      Some(FailedRegex(regex, line, output))
    }
  }
}
