package com.jakway.checkedshell.process.stream

import java.io.{BufferedWriter, File, FileWriter}

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.{JobOutputStream, StandardJobOutputStream}

trait RedirectionOperators[A] extends Redirectable[A] {
  def redirectToFile(descriptor: JobOutputStream,
                     to: File)
                    (implicit rc: RunConfiguration): A = {

    val writer = new BufferedWriter(new FileWriter(to))
    alterStreams(descriptor, writer)
  }

  def `1>`(to: File)(implicit rc: RunConfiguration): Unit =
    redirectToFile(StandardJobOutputStream.Stdout, to)

  def `2>`(to: File)(implicit rc: RunConfiguration): Unit =
    redirectToFile(StandardJobOutputStream.Stderr, to)
}
