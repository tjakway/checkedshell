package com.jakway.checkshell.test.framework

import com.jakway.checkedshell.data.output.ProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.test.framework.HasTestConfig

import scala.concurrent.Await

trait GetProgramOutput extends HasTestConfig {
  def getProgramOutput(jobOutput: JobOutput): ProgramOutput =
    Await.result(jobOutput, getTestConfig.futureTimeOut)
}
