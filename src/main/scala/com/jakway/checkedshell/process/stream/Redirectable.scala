package com.jakway.checkedshell.process.stream

import java.io.Writer

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.StreamWriters.StreamWriterMap
import com.jakway.checkedshell.data.{HasStreamWriters, JobOutputStream}

trait Redirectable[A] extends HasStreamWriters[A] {

  type AlterF = StreamWriterMap => JobOutputStream => Writer => StreamWriterMap

  private def replaceF: AlterF = writerMap => descriptor => writer => {
    writerMap.updated(descriptor, Seq(writer))
  }

  private def addF: AlterF = writerMap => descriptor => writer => {
    val existingWriters: Seq[Writer] = writerMap.getOrElse(descriptor, Seq())
    val newWriters: Seq[Writer] = existingWriters :+ writer
    writerMap.updated(descriptor, newWriters)
  }

  /**
   * normally we close streams when redirecting (hence why it's a redirect,
   * not a tee) but closeOtherStreamsOnRedirect can be used to change this behavior
   * @param rc
   * @return
   */
  private def getAlterFunction(rc: RunConfiguration): AlterF = {
    if(rc.streamsConfiguration.closeOtherStreamsOnRedirect) {
      replaceF
    } else {
      addF
    }
  }

  protected def alterStreams(newDescriptor: JobOutputStream,
                             newWriter: Writer)
                            (implicit rc: RunConfiguration): A = {
    val sw = getStreamWriters
    val alterF = getAlterFunction(rc)

    val newWriterMap = alterF(sw.streamWritersMap)(newDescriptor)(newWriter)

    val newSw = sw.copy(streamWritersMap = newWriterMap)
    copyWithStreamWriters(newSw)
  }
}
