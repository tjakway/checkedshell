package com.jakway.checkedshell.data

import org.slf4j.{Logger, LoggerFactory}


trait HasStreamWriters[A] {
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
    val streamWritersMap = streamWriters.streamWritersMap
    val resMap = additionalWriters.streamWritersMap.foldLeft(streamWritersMap) {
      case (acc, (descriptor, theseWriters)) => {
        val currentWriters = acc.getOrElse(descriptor, Seq.empty)
        val newWriters = currentWriters ++ theseWriters
        val newAcc = acc.updated(descriptor, newWriters)

        logger.trace(s"Added $additionalWriters writers to descriptor $descriptor")

        newAcc
      }
    }

    val res = streamWriters.copy(streamWritersMap = resMap)
    copyWithStreamWriters(res)
  }
}
