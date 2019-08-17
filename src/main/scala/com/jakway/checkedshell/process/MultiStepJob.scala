package com.jakway.checkedshell.process

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.ExecutionContext

class MultiStepJob(val runJobF: Option[ProgramOutput] => ExecutionContext => JobOutput)
  extends Job {

  protected def runJob(input: Option[ProgramOutput])
                      (implicit ec: ExecutionContext): JobOutput =
    runJobF(input)(ec)
}
