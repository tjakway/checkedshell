package com.jakway.checkedshell.process

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.process.Process.NativeProcessType

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process.ProcessLogger

/**
 * a job that runs as an external process
 * @param nativeProc
 */
class Process(val nativeProc: NativeProcessType)
  extends Job {

  override protected def runJob(input: Option[ProgramOutput])
                               (implicit ec: ExecutionContext): JobOutput = {
    Future {
      val bufLogger = new process.Process.BufLogger()

      //block until exit
      val exitCode: Int = nativeProc.!(
        ProcessLogger(bufLogger.onStdoutWrite,
                      bufLogger.onStderrWrite))

      new ProgramOutput(exitCode,
        bufLogger.stdoutBuf.toString(),
        bufLogger.stderrBuf.toString())
    }
  }
}

object Process {
  type NativeProcessType = scala.sys.process.ProcessBuilder

  class BufLogger {
    val stdoutBuf: StringBuilder = new StringBuilder()
    val stderrBuf: StringBuilder = new StringBuilder()

    def onStdoutWrite(s: String): Unit = stdoutBuf.append(s)
    def onStderrWrite(s: String): Unit = stderrBuf.append(s)
  }
}
