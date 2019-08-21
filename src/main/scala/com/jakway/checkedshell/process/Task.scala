package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.ExecutionContext

abstract class Task {
  def runJob(input: Option[ProgramOutput])
            (implicit rc: RunConfiguration,
             ec: ExecutionContext): JobOutput
}
