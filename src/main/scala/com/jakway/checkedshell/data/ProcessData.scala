package com.jakway.checkedshell.data

import java.io.Writer

import com.jakway.checkedshell.data.ProcessData.StreamWriters
import com.jakway.checkedshell.process.Process.NativeProcessType

case class ProcessData(nativeProcess: NativeProcessType,
                       streamWriters: StreamWriters)

object ProcessData {
  type StreamWriters = Map[JobOutputStream, Writer]
}
