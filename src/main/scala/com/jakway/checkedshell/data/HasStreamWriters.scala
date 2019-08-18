package com.jakway.checkedshell.data

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.error.ErrorData
import com.jakway.checkedshell.error.cause.{CloseStreamError, CloseStreamErrors}
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}


trait HasStreamWriters[A] {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

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

  def closeAllStreams(implicit rc: RunConfiguration): Unit = {
    val streamWriters: StreamWriters = getStreamWriters
    val empty: Seq[Throwable] = Seq.empty
    val errs = streamWriters.streamWritersMap.foldLeft(empty) {
      case (acc, (descriptor, writers)) => {
        acc ++ writers.foldLeft(Seq.empty: Seq[Throwable]) {
          case (wAcc, writer) =>
            Try(writer.close()) match {
              case Success(_) => wAcc
              case Failure(t) => {
                val closeStreamError: Throwable =
                  new CloseStreamError(s"Exception thrown" +
                    s" while closing stream $descriptor")
                    .initCause(t)

                wAcc :+ closeStreamError
              }
            }
        }
      }
    }

    val combinedError = CloseStreamErrors(errs)

    val errorData: ErrorData = ErrorData(Some("Stream close errors"), combinedError)
    rc.errorBehavior.handleError(errorData)
  }
}
