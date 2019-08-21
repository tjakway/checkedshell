package com.jakway.checkedshell.common

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.{ProgramOutput, StreamWriters}
import com.jakway.checkedshell.process.Job
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.ExecutionContext

class Echo extends Job {
  override protected def runJob(input: Option[ProgramOutput])(implicit rc: RunConfiguration, ec: ExecutionContext): JobOutput = ???

  override protected def getStreamWriters: StreamWriters = ???

  override protected def copyWithStreamWriters(newStreamWriters: StreamWriters): Job = ???
}
