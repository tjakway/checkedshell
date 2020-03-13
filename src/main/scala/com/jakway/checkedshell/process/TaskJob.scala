package com.jakway.checkedshell.process
import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.data.{HasStreamWriters, StreamWriters}
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.ExecutionContext

case class TaskJob(task: Task,
                   streamWriters: StreamWriters = HasStreamWriters.defaultStreamWriters)
  extends Job {
  override protected def runJob(input: Option[FinishedProgramOutput])
                               (implicit rc: RunConfiguration,
                                ec: ExecutionContext): JobOutput = task.runJob(input)(rc)(ec)

  override protected def getStreamWriters: StreamWriters = streamWriters

  override protected def copyWithStreamWriters(newStreamWriters: StreamWriters): Job =
    copy(streamWriters = newStreamWriters)
}

