package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.StreamWriters
import com.jakway.checkedshell.data.output.ProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.ExecutionContext

class MultiStepJob[A](val runJobF: Job.RunJobF,
                      val streamWriters: StreamWriters)
  extends Job {

  protected def runJob(input: Option[ProgramOutput])
                      (implicit rc: RunConfiguration,
                                ec: ExecutionContext): JobOutput =
    runJobF(input)(rc)(ec)

  override protected def getStreamWriters: StreamWriters = streamWriters

  override protected def copyWithStreamWriters(newStreamWriters: StreamWriters): MultiStepJob[A] =
    new MultiStepJob[A](runJobF, newStreamWriters)
}
