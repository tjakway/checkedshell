package com.jakway.checkedshell.process.stream

import java.io.{BufferedWriter, File, FileWriter}

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.{JobOutputStream, StandardJobOutputStream}
import com.jakway.checkedshell.process.stream.RedirectionOperators.{DevNull, SpecialFile}

trait RedirectionOperators[A] extends Redirectable[A] {
  def redirectToFile(descriptor: JobOutputStream,
                     to: File)
                    (implicit rc: RunConfiguration): A = {

    val writer = new BufferedWriter(new FileWriter(to))
    alterStreams(descriptor, writer)
  }

  private def handleSpecialFile(descriptor: JobOutputStream,
                                specialFile: SpecialFile)
                               (implicit rc: RunConfiguration): A = {
    specialFile match {
      case DevNull => closeStreams(descriptor)
    }
  }

  def `1>`(to: File)(implicit rc: RunConfiguration): Unit =
    redirectToFile(StandardJobOutputStream.Stdout, to)

  def `2>`(to: File)(implicit rc: RunConfiguration): Unit =
    redirectToFile(StandardJobOutputStream.Stderr, to)

  def `1>`(to: SpecialFile)(implicit rc: RunConfiguration): Unit =
    handleSpecialFile(StandardJobOutputStream.Stdout, to)

  def `2>`(to: SpecialFile)(implicit rc: RunConfiguration): Unit =
    handleSpecialFile(StandardJobOutputStream.Stderr, to)
}

object RedirectionOperators {
  sealed trait SpecialFile

  case object DevNull extends SpecialFile
}
