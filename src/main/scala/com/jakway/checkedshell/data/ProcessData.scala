package com.jakway.checkedshell.data

import java.io.Writer

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.ProcessData.StreamWriters
import com.jakway.checkedshell.error.ErrorData
import com.jakway.checkedshell.error.cause.{CloseStreamError, CloseStreamErrors}
import com.jakway.checkedshell.process.Process.NativeProcessType

import scala.util.{Failure, Success, Try}

case class ProcessData(nativeProcess: NativeProcessType,
                       streamWriters: StreamWriters) {

  def closeAllStreams(implicit rc: RunConfiguration): Unit = {
    val empty: Seq[Throwable] = Seq.empty
    val errs = streamWriters.foldLeft(empty) {
      case (acc, (descriptor, writer)) => {
        Try(writer.close()) match {
          case Success(_) => acc
          case Failure(t) => {
            val closeStreamError: Throwable =
              new CloseStreamError(s"Exception thrown" +
                s" while closing stream $descriptor")
              .initCause(t)

            acc :+ closeStreamError
          }
        }
      }
    }

    val combinedError = CloseStreamErrors(errs)

    val errorData: ErrorData = ErrorData(Some("Stream close errors"), combinedError)
    rc.errorBehavior.handleError(errorData)
  }
}

object ProcessData {
  type StreamWriters = Map[JobOutputStream, Writer]
}
