package com.jakway.checkedshell.process

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.process.Process.NativeProcessType

import scala.concurrent.ExecutionContext

/**
 * a job that runs as an external process
 * @param nativeProc
 * @param job
 */
class Process(val nativeProc: NativeProcessType,
              val job: Option[ProgramOutput] => ExecutionContext => JobOutput)
  extends Job {

  override protected def runJob(input: Option[ProgramOutput])(implicit ec: ExecutionContext): JobOutput =
    job(input)(ec)

  override protected def copyWithNewRunJob(newRunJob: Option[ProgramOutput] => ExecutionContext => JobOutput): Job = {
    new Process(nativeProc, job)
  }
}

object Process {
  type NativeProcessType = scala.sys.process.Process

  
}
