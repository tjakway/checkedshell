package com.jakway.checkedshell.process.stream

import java.io.StringWriter

import com.jakway.checkedshell.data.StreamWriters.StreamWriterMap
import com.jakway.checkedshell.data.{ProcessData, StandardJobOutputDescriptor, StreamWriters}

class StandardStreamWriters(val initialSize: Int =
                              StandardStreamWriters.defaultInitialSize) {
  val stdoutWriter: StringWriter = new StringWriter(initialSize)
  val stderrWriter: StringWriter = new StringWriter(initialSize)

  /**
   * need to explicitly cast String -> CharSequence for some reason
   * @param writer
   * @param x
   */
  private def writeStringWriter(writer: StringWriter)(x: String): Unit =
    writer.append(x: CharSequence)

  def writeStdout: String => Unit = writeStringWriter(stdoutWriter)
  def writeStderr: String => Unit = writeStringWriter(stderrWriter)

  //***********WARNING***********
  // changing this to Map { ... } causes it to ignore this first entry!
  // (reasons unknown)
  //*****************************
  val writerMap: StreamWriterMap = Map(
    StandardJobOutputDescriptor.Stdout -> Set(stdoutWriter),
    StandardJobOutputDescriptor.Stderr -> Set(stderrWriter)
  )

  val streamWriters: StreamWriters = StreamWriters(
    Some(stdoutWriter), Some(stderrWriter), writerMap)
}

object StandardStreamWriters {
  val defaultInitialSize: Int = 4096
}