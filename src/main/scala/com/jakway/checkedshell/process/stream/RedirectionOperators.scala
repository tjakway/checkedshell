package com.jakway.checkedshell.process.stream

import java.io.{BufferedWriter, File, FileWriter}

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.{JobOutputDescriptor, StandardJobOutputDescriptor}
import com.jakway.checkedshell.process.stream.RedirectionOperators.{DevNull, SpecialFile}

trait RedirectionOperators[A] extends Redirectable[A] {
  def redirectToFile(descriptor: JobOutputDescriptor,
                     to: File)
                    (implicit rc: RunConfiguration): A = {

    val writer = new BufferedWriter(new FileWriter(to))
    alterStreams(descriptor, writer)
  }

  private def handleSpecialFile(descriptor: JobOutputDescriptor,
                                specialFile: SpecialFile)
                               (implicit rc: RunConfiguration): A = {
    specialFile match {
      case DevNull => closeStreams(descriptor)
    }
  }

  def `1>`(to: File)(implicit rc: RunConfiguration): Unit =
    redirectToFile(StandardJobOutputDescriptor.Stdout, to)

  def `2>`(to: File)(implicit rc: RunConfiguration): Unit =
    redirectToFile(StandardJobOutputDescriptor.Stderr, to)

  def `1>`(to: SpecialFile)(implicit rc: RunConfiguration): Unit =
    handleSpecialFile(StandardJobOutputDescriptor.Stdout, to)

  def `2>`(to: SpecialFile)(implicit rc: RunConfiguration): Unit =
    handleSpecialFile(StandardJobOutputDescriptor.Stderr, to)
}

object RedirectionOperators {
  sealed trait SpecialFile

  case object DevNull extends SpecialFile
}
