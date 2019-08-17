package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.Future

trait Job {
  def run(input: Option[ProgramOutput])(implicit runConfiguration: RunConfiguration): JobOutput
}

object Job {
  type JobOutput = Future[ProgramOutput]
}
