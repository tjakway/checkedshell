package com.jakway.checkedshell.process.stream

import java.io.Writer

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.StreamWriters.StreamWriterMap
import com.jakway.checkedshell.data.{HasStreamWriters, JobOutputStream}
import com.jakway.checkedshell.error.cause.CloseStreamError

trait Redirectable[A] extends HasStreamWriters[A] {

  type AlterF = StreamWriterMap => JobOutputStream => Writer => StreamWriterMap

  private object AlterFunctions {
    private def composeAlterFunctions: AlterF => AlterF => AlterF = (left: AlterF) => (right: AlterF) =>
      (swMap: StreamWriterMap) => (descriptor: JobOutputStream) => (writer: Writer) => {

        //compositions go right-to-left
        val rightRes = right(swMap)(descriptor)(writer)
        left(rightRes)(descriptor)(writer)
      }

    private def replaceF: AlterF = writerMap => descriptor => writer => {
      writerMap.updated(descriptor, Seq(writer))
    }

    private def addF: AlterF = writerMap => descriptor => writer => {
      val existingWriters: Seq[Writer] = writerMap.getOrElse(descriptor, Seq())
      val newWriters: Seq[Writer] = existingWriters :+ writer
      writerMap.updated(descriptor, newWriters)
    }

    private def conditionalAlterFunction(
                condition: StreamWriterMap => JobOutputStream => Writer => Boolean,
                ifTrue: AlterF,
                otherwise: AlterF): AlterF = {

      (swMap: StreamWriterMap) => (descriptor: JobOutputStream) => (writer: Writer) => {
        if(condition(swMap)(descriptor)(writer)) {
          ifTrue(swMap)(descriptor)(writer)
        } else {
          otherwise(swMap)(descriptor)(writer)
        }
      }
    }

    private def writerIsNullCondition: StreamWriterMap => JobOutputStream => Writer => Boolean = {
      (swMap: StreamWriterMap) => (descriptor: JobOutputStream) => (writer: Writer) => {
        writer == null
      }
    }

    /**
     * close all streams for the passed descriptor
     * @return
     */
    def closeStreamsF: AlterF =
      (swMap: StreamWriterMap) => (descriptor: JobOutputStream) => (writer: Writer) => {
        swMap
          .get(descriptor)
          .foreach { writers =>
            //wrap and rethrow exceptions caught while closing stream
            //so our RunConfiguration can decide how to handle it in the calling method
            try {
              writers.foreach(_.close())
            } catch {
              case t: Throwable => throw CloseStreamError.wrapAsCloseStreamError(t)
            }
          }

        swMap
    }

    /**
     * self explanatory
     * @return
     */
    private def doNothingF: AlterF =
      (swMap: StreamWriterMap) => (descriptor: JobOutputStream) => (writer: Writer) => {
        swMap
      }

    /**
     * normally we close streams when redirecting (hence why it's a redirect,
     * not a tee) but closeOtherStreamsOnRedirect can be used to change this behavior
     * @param rc
     * @return
     */
    def getAlterFunction(rc: RunConfiguration): AlterF = {
      if(rc.streamsConfiguration.closeOtherStreamsOnRedirect) {
        replaceF
      } else {
        addF
      }
    }
  }

  protected def closeStreams(forDescriptor: JobOutputStream)
                            (implicit rc: RunConfiguration): A = {
    val sw = getStreamWriters
    val newMap = AlterFunctions.closeStreamsF(sw.streamWritersMap)(forDescriptor)(null)

    copyWithStreamWriters(sw.copy(streamWritersMap = newMap))
  }


  protected def alterStreams(newDescriptor: JobOutputStream,
                             newWriter: Writer)
                            (implicit rc: RunConfiguration): A = {
    val sw = getStreamWriters
    val alterF = AlterFunctions.getAlterFunction(rc)

    val newWriterMap = alterF(sw.streamWritersMap)(newDescriptor)(newWriter)

    val newSw = sw.copy(streamWritersMap = newWriterMap)
    copyWithStreamWriters(newSw)
  }
}
