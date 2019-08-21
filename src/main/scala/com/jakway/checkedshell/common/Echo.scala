package com.jakway.checkedshell.common

import java.util.Formatter

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.{ProgramOutput, StreamWriters}
import com.jakway.checkedshell.process.{Job, Task}
import com.jakway.checkedshell.process.Job.{JobOutput, RunJobF}

import scala.concurrent.{ExecutionContext, Future}

class Echo(val printTrailingNewLine: Boolean)
  extends Task {
  override def runJob: RunJobF = Echo(printTrailingNewLine)
}

object Echo {
  def apply(printTrailingNewLine: Boolean)
           (optInput: Option[ProgramOutput])(rc: RunConfiguration,
                                             ec: ExecutionContext): JobOutput =
    Future {
      val fmt: Formatter = new Formatter()

      optInput.foreach { input =>
        if(!input.stdout.isEmpty) {
          fmt.format("%s", input.stdout)
        }
      }

      if(printTrailingNewLine) {
        fmt.format("\n")
      } else {}

      new ProgramOutput(0, toString, "")
  }
}
