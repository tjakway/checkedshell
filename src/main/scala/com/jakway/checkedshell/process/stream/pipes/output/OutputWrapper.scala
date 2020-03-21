package com.jakway.checkedshell.process.stream.pipes.output

import java.io.{OutputStream, Writer}

import com.jakway.checkedshell.process.stream.pipes.StreamWrapper

trait OutputWrapper extends StreamWrapper  {
  val encoding: String
  val optDescription: Option[String]

  def getWriter(enc: String = encoding): Writer
  def getOutputStream: OutputStream
}

object OutputWrapper {
  def apply(os: OutputStream,
            encoding: String,
            optDescription: Option[String]): OutputWrapper =
    new OutputStreamWrapper(os, encoding, optDescription)

  def apply(os: OutputStream,
            encoding: String,
            description: String): OutputWrapper =
    apply(os, encoding, Some(description))
}
