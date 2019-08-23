package com.jakway.checkedshell.data

import java.io.Writer

import com.jakway.checkedshell.data.StreamWriters.StreamWriterMap

import scala.sys.process.{ProcessLogger => SProcessLogger}

case class StreamWriters(stdoutWriter: Option[Writer],
                         stderrWriter: Option[Writer],
                         streamWritersMap: StreamWriterMap) {

  def toProcessLogger: SProcessLogger = {
    //look up writers in the map and write the data to all of them
    def writeKeyedWriter: JobOutputDescriptor => String => Unit = key => data => {
      streamWritersMap.get(key).foreach { writers =>
        val cs: CharSequence = data
        writers.foreach(_.append(cs + System.lineSeparator()))
      }
    }

    def writeStdout = writeKeyedWriter(StandardJobOutputDescriptor.Stdout)
    def writeStderr = writeKeyedWriter(StandardJobOutputDescriptor.Stderr)

    SProcessLogger(writeStdout, writeStderr)
  }
}

object StreamWriters {
  type StreamWriterMap = Map[JobOutputDescriptor, Seq[Writer]]
}