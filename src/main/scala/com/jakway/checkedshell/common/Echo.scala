package com.jakway.checkedshell.common

import java.util.Formatter

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.RunJobF
import com.jakway.checkedshell.process.Task

import scala.concurrent.{ExecutionContext, Future}

//TODO: rewrite to actually behave like echo...
//i.e. don't read from standard input, take args in the constructor and print them
class Echo(val printTrailingNewLine: Boolean = true, val args: Seq[Object])
  extends Task {
  override def runJob: RunJobF = Echo.apply(printTrailingNewLine)(args)

  def this(printTrailingNewLine: Boolean, args: String*) {
    this(printTrailingNewLine, args)
  }
}

object Echo {
  def apply: Boolean => Seq[Object] => RunJobF = printTrailingNewLine => optInput =>
    (pRc: RunConfiguration, pEc: ExecutionContext) => {

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
