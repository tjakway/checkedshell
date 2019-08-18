package com.jakway.checkedshell.process.stream

import java.io.StringWriter

import com.jakway.checkedshell.data.StreamWriters.StreamWriterMap
import com.jakway.checkedshell.data.{ProcessData, StandardJobOutputStream, StreamWriters}

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

  val writerMap: StreamWriterMap = Map {
    StandardJobOutputStream.Stdout -> Seq(stdoutWriter)
    StandardJobOutputStream.Stderr -> Seq(stderrWriter)
  }

  val streamWriters: StreamWriters = StreamWriters(
    Some(stdoutWriter), Some(stderrWriter), writerMap)
}

object StandardStreamWriters {
  val defaultInitialSize: Int = 4096
}