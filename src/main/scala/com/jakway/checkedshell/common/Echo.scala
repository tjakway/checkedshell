package com.jakway.checkedshell.common

import java.util.Formatter

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.RunJobF
import com.jakway.checkedshell.process.Task

import scala.concurrent.Future

class Echo(val printTrailingNewLine: Boolean)
  extends Task {
  override def runJob: RunJobF = Echo.apply(printTrailingNewLine)
}

object Echo {
  def apply: Boolean => RunJobF = printTrailingNewLine => optInput => (pRc, pEc) => {
    implicit val ec = pEc
    Future {
      val fmt: Formatter = new Formatter()

      optInput.foreach { input =>
        if (!input.stdout.isEmpty) {
          fmt.format("%s", input.stdout)
        }
      }

      if (printTrailingNewLine) {
        fmt.format("\n")
      } else {}

      new ProgramOutput(0, toString, "")
    }
  }
}
