package com.jakway.checkedshell.process.stream.pipes.output

import java.io.{OutputStream, Writer}
import java.util.Formatter

trait OutputWrapper {
  val encoding: String
  val description: Option[String]

  def getWriter(enc: String = encoding): Writer
  def getOutputStream: OutputStream

  override def toString: String = {
    val fmt: Formatter = {
      val sb = new StringBuffer()
      new Formatter(sb)
    }

    fmt.format("%s(%s)",
      getClass.getSimpleName,
      description.getOrElse("<no description>"))
      .toString
  }
}
