package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.error.cause.{ErrorCause, FailedRegex}

import scala.util.matching.Regex

abstract class RegexCheck(val regex: Regex) extends FieldCheck[String] {
  override protected def check(a: String): Option[FinishedProgramOutput => ErrorCause] = {
    if(regex.pattern.matcher(a).matches) {
      None
    } else {
      Some((output: FinishedProgramOutput) => FailedRegex(regex, a, output))
    }
  }
}
