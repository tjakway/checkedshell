package com.jakway.checkedshell.process.stream.pipes

import java.util.Formatter

trait StreamWrapper {
  val optDescription: Option[String]

  override def toString: String = {
    val fmt: Formatter = {
      val sb = new StringBuffer()
      new Formatter(sb)
    }

    fmt.format("%s(%s)",
      getClass.getSimpleName,
      optDescription.getOrElse("<no description>"))
      .toString
  }
}
