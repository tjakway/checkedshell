package com.jakway.checkedshell.process.stream.pipes.output

import java.io.{OutputStream, OutputStreamWriter, Writer}

class OutputStreamWrapper(val encoding: String,
                          val description: Option[String],
                          protected val os: OutputStream)
  extends OutputWrapper {

  override def getOutputStream: OutputStream = os

  override def getWriter(enc: String): Writer = {
    new OutputStreamWriter(os, encoding)
  }
}

object OutputStreamWrapper {
  type StdoutWrapper = OutputStreamWrapper
  type StderrWrapper = OutputStreamWrapper
}
