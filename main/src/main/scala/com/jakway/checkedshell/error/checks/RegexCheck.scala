package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.output.{FinishedProgramOutput, ProgramOutput}
import com.jakway.checkedshell.error.cause.{ErrorCause, FailedRegex}
import com.jakway.checkedshell.error.checks.FinishedOutputCheck.GetOutput
import com.jakway.checkedshell.error.checks.PerLineCheck.GetLines

import scala.util.matching.Regex

trait RegexCheck extends OutputCheck {
  def regex: Regex

  protected def regexMatches(x: String): Boolean =
    regex.pattern.matcher(x).matches()

  protected def mkFailedRegexError:
    (Regex, String, ProgramOutput) => ErrorCause =
    FailedRegex.apply
}

object RegexCheck {
  class PerLineRegexCheck(val getLines: GetLines,
                          override val regex: Regex,
                          override val stopEarly: Boolean)
    extends RegexCheck with PerLineCheck {
    override protected def checkLine: ProgramOutput =>
      String => Option[ErrorCause] = output => line => {
      if(regexMatches(line)) {
        None
      } else {
        Some(mkFailedRegexError(regex, line, output))
      }
    }
  }

  class TotalRegexCheck(val getOutput: GetOutput,
                        override val regex: Regex)
    extends RegexCheck with FinishedOutputCheck {
    override def checkFinishedOutput(output: FinishedProgramOutput)
                                    (implicit rc: RunConfiguration):
      Set[ErrorCause] = {

      val toCheck = getOutput(output)
      if(regexMatches(toCheck)) {
        Set.empty
      } else {
        Set(mkFailedRegexError(regex, toCheck, output))
      }
    }
  }
}
