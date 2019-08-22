package com.jakway.checkedshell.common

import java.util.Formatter

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.RunJobF
import com.jakway.checkedshell.process.Task

import scala.concurrent.{ExecutionContext, Future}

//TODO: rewrite to actually behave like echo...
//i.e. don't read from standard input, take args in the constructor and print them
class Echo(val printTrailingNewLine: Boolean = Echo.defaultPrintTrailingNewLine,
           val args: Seq[Object] = Seq())
  extends Task {
  override def runJob: RunJobF = Echo.echo(printTrailingNewLine)(args)
}

object Echo {
  val argSeparator: String = " "
  val defaultPrintTrailingNewLine: Boolean = true

  /**
   * variadic constructor
   * @param printTrailingNewLine
   * @param args
   * @return
   */
  def apply(printTrailingNewLine: Boolean,
            args: String*): Echo = new Echo(printTrailingNewLine, args)

  private def echo: Boolean => Seq[Object] => RunJobF =
    printTrailingNewLine => (args: Seq[Object]) => optInput =>
    (pRc: RunConfiguration) => (pEc: ExecutionContext) => {

      //optInput is ignored (echo doesn't care about stdin)

      implicit val ec: ExecutionContext = pEc
      Future {
        val fmt: Formatter = new Formatter()

        //print each argument with a space between them
        args.headOption.foreach { head =>
          fmt.format("%s", head)
        }

        args.tail.foreach { tail =>
          fmt.format("%s%s", argSeparator, tail)
        }

        if (printTrailingNewLine) {
          fmt.format("\n")
        } else {}

        new ProgramOutput(0, toString, "")
      }
  }
}
