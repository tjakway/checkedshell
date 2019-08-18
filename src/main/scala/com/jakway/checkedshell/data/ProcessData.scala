package com.jakway.checkedshell.data

import java.io.Writer

import com.jakway.checkedshell.data.ProcessData.StreamWriters
import com.jakway.checkedshell.process.Process.NativeProcessType
import org.slf4j.{Logger, LoggerFactory}

case class ProcessData(nativeProcess: NativeProcessType,
                       streamWriters: StreamWriters) {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  /**
   * @param additionalWriters
   * @return a new copy of ProcessData will the passed StreamWriters added
   *         to the existing writers
   */
  def addStreamWriters(additionalWriters: StreamWriters): ProcessData = {
    val res = additionalWriters.foldLeft(streamWriters) {
      case (acc, (descriptor, theseWriters)) => {
        val currentWriters = acc.getOrElse(descriptor, Seq.empty)
        val newWriters = currentWriters ++ theseWriters
        val newAcc = acc.updated(descriptor, newWriters)

        logger.trace(s"Added $additionalWriters writers to descriptor $descriptor")

        newAcc
      }
    }

    copy(streamWriters = res)
  }
}

object ProcessData {
  type StreamWriters = Map[JobOutputStream, Seq[Writer]]
}
