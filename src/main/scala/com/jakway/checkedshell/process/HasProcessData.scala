package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.ProcessData
import com.jakway.checkedshell.error.ErrorData
import com.jakway.checkedshell.error.cause.{CloseStreamError, CloseStreamErrors}

import scala.util.{Failure, Success, Try}

trait HasProcessData[A] {
  def copyWithProcessData(newProcessData: ProcessData): A

  def closeAllStreams(processData: ProcessData)(implicit rc: RunConfiguration): Unit = {
    val empty: Seq[Throwable] = Seq.empty
    val errs = processData.streamWriters.foldLeft(empty) {
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
