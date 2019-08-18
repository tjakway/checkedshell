package com.jakway.checkedshell.process

import java.io.File

import com.jakway.checkedshell.data.{ProcessData, ProgramOutput}
import com.jakway.checkedshell.process
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.process.Process.NativeProcessType

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process.{Process => SProcess, ProcessLogger => SProcessLogger}

/**
 * a job that runs as an external process
 * implements stream writes using a buffer logger by default
 */
class Process(val processData: ProcessData)
  extends Job {

  lazy val bufLogger = new process.Process.BufLogger() {}
  override def onStdoutWrite(s: String): Unit = bufLogger.stdoutBuf.append(s)
  override def onStderrWrite(s: String): Unit = bufLogger.stderrBuf.append(s)

  override def getStdout(): String = bufLogger.stdoutBuf.toString()
  override def getStderr(): String = bufLogger.stderrBuf.toString()

  //we copy some common constructors here for convenience
  //for others, use the scala.sys.process.Process companion object's apply methods
  //directly
  def this(args: Seq[String]) {
    this(SProcess(args))
  }

  def this(args: Seq[String], cwd: Option[File]) {
    this(SProcess(args, cwd))
  }

  def this(proc: String, args: Seq[String], cwd: Option[File]) {
    this(SProcess(Seq(proc) ++ args, cwd))
  }

  def this(proc: String, args: Seq[String]) {
    this(proc, args, None)
  }

  override protected def runJob(input: Option[ProgramOutput])
                               (implicit ec: ExecutionContext): JobOutput = {
    Future {
      //block until exit
      val exitCode: Int = nativeProc.!(
        SProcessLogger(onStdoutWrite,
                      onStderrWrite))

      new ProgramOutput(exitCode,
        getStdout(),
        getStderr())
    }
  }
}

object Process {
  type NativeProcessType = scala.sys.process.ProcessBuilder

  trait BufLogger extends StdoutStreamWriter with StderrStreamWriter {
    val stdoutBuf: StringBuilder = new StringBuilder()
    val stderrBuf: StringBuilder = new StringBuilder()

    override def onStdoutWrite(s: String): Unit = stdoutBuf.append(s)
    override def onStderrWrite(s: String): Unit = stderrBuf.append(s)

    override def getStdout(): String = stdoutBuf.toString()
    override def getStderr(): String = stderrBuf.toString()
  }
}
