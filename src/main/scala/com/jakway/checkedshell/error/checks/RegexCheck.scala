package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.error.cause.{ErrorCause, FailedRegex}

import scala.util.matching.Regex

abstract class RegexCheck(val regex: Regex) extends FieldCheck[String] {
  override protected def check(a: String): Option[ProgramOutput => ErrorCause] = {
    if(regex.pattern.matcher(a).matches) {
      None
    } else {
      Some((output: ProgramOutput) => FailedRegex(regex, a, output))
    }
  }
}
