package com.jakway.checkedshell.error

import java.util.Formatter

import scala.collection.immutable.StringOps

class ErrorPrinter(val indentSpaces: Int = ErrorPrinter.defaultIndentSpaces) {

  private def indentFormatString(level: Int): String =
    "%" + (indentSpaces * level).toString + "s"

  def printExceptions(xs: Seq[Throwable]): String = {
    val fmt: Formatter = new Formatter()
    if(xs.nonEmpty) {
      fmt.format(s"${xs.length} Exceptions Thrown:")
      xs.foreach { x =>
        val lines = {
          //see https://stackoverflow.com/questions/52815574/scala-12-x-and-java-11-string-lines-how-to-force-the-implicit-conversion-in-a
          val ops: StringOps = x.toString
          ops.lines.toSeq
        }

        lines.headOption.foreach(fmt.format(indentFormatString(1), _))
        lines.tail.foreach(fmt.format(indentFormatString(2), _))
      }
    }

    fmt.toString
  }

}

object ErrorPrinter {
  val defaultIndentSpaces: Int = 4
}
