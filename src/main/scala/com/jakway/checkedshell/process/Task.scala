package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.process.Job.{JobOutput, RunJobF}

import scala.concurrent.ExecutionContext

abstract class Task {
  def runJob: RunJobF
}
