package com.jakway.checkedshell.data

import java.io.Writer

import com.jakway.checkedshell.data.WithStreamWriters.StreamWriters
import org.slf4j.{Logger, LoggerFactory}

trait WithStreamWriters[A] {
  private val logger: Logger = LoggerFactory.getLogger(getClass())

  protected def getStreamWriters: StreamWriters
  protected def copyWithStreamWriters(newStreamWriters: StreamWriters): A

  /**
   * @param additionalWriters
   * @return a new copy of ProcessData will the passed StreamWriters added
   *         to the existing writers
   */
  def addStreamWriters(additionalWriters: StreamWriters): A = {
    val streamWriters = getStreamWriters
    val res = additionalWriters.foldLeft(streamWriters) {
      case (acc, (descriptor, theseWriters)) => {
        val currentWriters = acc.getOrElse(descriptor, Seq.empty)
        val newWriters = currentWriters ++ theseWriters
        val newAcc = acc.updated(descriptor, newWriters)

        logger.trace(s"Added $additionalWriters writers to descriptor $descriptor")

        newAcc
      }
    }

    copyWithStreamWriters(res)
  }
}

object WithStreamWriters {
  type StreamWriters = Map[JobOutputStream, Seq[Writer]]
}