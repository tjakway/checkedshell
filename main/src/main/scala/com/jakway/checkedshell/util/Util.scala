package com.jakway.checkedshell.util

import java.io.{PrintWriter, StringWriter}

import scala.collection.JavaConverters

object Util {
  lazy val lineSeparator: String = System.getProperty("line.separator")
  /**
   * see https://stackoverflow.com/questions/1149703/how-can-i-convert-a-stack-trace-to-a-string
   * @return
   */
  def throwableToString(t: Throwable, maxLines: Int = 15) = {
    val sw: StringWriter = new StringWriter()
    val pw: PrintWriter  = new PrintWriter(sw)
    t.printStackTrace(pw)

    JavaConverters.asScalaIterator(sw.toString().lines.iterator())
      //java stream doesn't have take
        .take(maxLines)
        .mkString(lineSeparator)
  }
}
