package com.jakway.checkedshell.process.stream.pipes.output

import java.io.{OutputStream, Writer}

import com.jakway.checkedshell.process.stream.pipes.StreamWrapper

trait OutputWrapper extends StreamWrapper  {
  val encoding: String
  val description: Option[String]

  def getWriter(enc: String = encoding): Writer
  def getOutputStream: OutputStream
}
