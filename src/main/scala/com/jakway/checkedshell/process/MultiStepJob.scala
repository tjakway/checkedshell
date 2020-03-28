package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.StreamWriters
import com.jakway.checkedshell.data.output.{FinishedProgramOutput, ProgramOutput}
import com.jakway.checkedshell.process.Job.{JobInput, JobOutput}

import scala.concurrent.ExecutionContext

/**
 * A wrapper class for when [[execJobF]] is a composition of [[Job.ExecJobF]]
 * methods from multiple processes
 * @param execJobF runs two or more [[Job.ExecJobF]] functions from
 *                other classes
 * @param streamWriters
 * @tparam A
 */
class MultiStepJob[A](val execJobF: Job.ExecJobF,
                      val streamWriters: StreamWriters,
                      val optDescription: Option[String] = None)
  extends Job {

  override protected def execJob(
   input: JobInput)(
   implicit rc: RunConfiguration,
            ec: ExecutionContext): ProgramOutput = {
    execJobF(input)(rc)(ec)
  }

  override protected def getStreamWriters: StreamWriters = streamWriters

  override protected def copyWithStreamWriters(
    newStreamWriters: StreamWriters): MultiStepJob[A] =
    new MultiStepJob[A](execJobF, newStreamWriters)
}
