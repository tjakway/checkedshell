package com.jakway.checkedshell.process

import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.process.Process.NativeProcessType

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process.ProcessLogger

trait StdoutStreamWriter {
  def onStdoutWrite(s: String): Unit
  def getStdout(): String
}

trait StderrStreamWriter {
  def onStderrWrite(s: String): Unit
  def getStderr(): String
}

/**
 * a job that runs as an external process
 * implements stream writes using a buffer logger by default
 * @param nativeProc
 */
class Process(val nativeProc: NativeProcessType)
  extends Job
    with StdoutStreamWriter
    with StderrStreamWriter {

  lazy val bufLogger = new process.Process.BufLogger()
  override def onStdoutWrite(s: String): Unit = bufLogger.stdoutBuf.append(s)
  override def onStderrWrite(s: String): Unit = bufLogger.stderrBuf.append(s)

  override def getStdout(): String = bufLogger.stdoutBuf.toString()
  override def getStderr(): String = bufLogger.stderrBuf.toString()

  override protected def runJob(input: Option[ProgramOutput])
                               (implicit ec: ExecutionContext): JobOutput = {
    Future {
      //block until exit
      val exitCode: Int = nativeProc.!(
        ProcessLogger(onStdoutWrite,
                      onStderrWrite))

      new ProgramOutput(exitCode,
        getStdout(),
        getStderr())
    }
  }
}

object Process {
  type NativeProcessType = scala.sys.process.ProcessBuilder

  class BufLogger extends StdoutStreamWriter with StderrStreamWriter {
    val stdoutBuf: StringBuilder = new StringBuilder()
    val stderrBuf: StringBuilder = new StringBuilder()

    override def onStdoutWrite(s: String): Unit = stdoutBuf.append(s)
    override def onStderrWrite(s: String): Unit = stderrBuf.append(s)

    override def getStdout(): String = stdoutBuf.toString()
    override def getStderr(): String = stderrBuf.toString()
  }
}
