package com.jakway.checkedshell.process.stream.pipes.output

import java.io.{OutputStream, OutputStreamWriter, Writer}

class OutputStreamWrapper(protected val os: OutputStream,
                          val encoding: String,
                          val optDescription: Option[String])
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
