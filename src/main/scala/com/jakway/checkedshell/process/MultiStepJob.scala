package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.StreamWriters
import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.ExecutionContext

/**
 * A wrapper class for when [[runJobF]] is a composition of [[Job.RunJobF]]
 * methods from multiple processes
 * @param runJobF runs two or more [[Job.RunJobF]] functions from
 *                other classes
 * @param streamWriters
 * @tparam A
 */
class MultiStepJob[A](val runJobF: Job.RunJobF,
                      val streamWriters: StreamWriters)
  extends Job {

  protected def runJob(input: Option[FinishedProgramOutput])
                      (implicit rc: RunConfiguration,
                                ec: ExecutionContext): JobOutput =
    runJobF(input)(rc)(ec)

  override protected def getStreamWriters: StreamWriters = streamWriters

  override protected def copyWithStreamWriters(newStreamWriters: StreamWriters): MultiStepJob[A] =
    new MultiStepJob[A](runJobF, newStreamWriters)
}
