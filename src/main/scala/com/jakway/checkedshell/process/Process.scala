package com.jakway.checkedshell.process

import java.io.File

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.{ProcessData, ProgramOutput}
import com.jakway.checkedshell.process
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.process.Process.NativeProcessType
import com.jakway.checkedshell.process.stream.StandardStreamWriters

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process.{Process => SProcess, ProcessLogger => SProcessLogger}

/**
 * a job that runs as an external process
 * implements stream writes using a buffer logger by default
 */
class Process(val processData: ProcessData,
              private val standardStreamWriters: StandardStreamWriters)
  extends Job
    with HasProcessData[Process] {

  override def copyWithProcessData(newProcessData: ProcessData): Process =
    new Process(newProcessData, standardStreamWriters)

  override protected def runJob(input: Option[ProgramOutput])
                               (implicit rc: RunConfiguration,
                                         ec: ExecutionContext): JobOutput = {
    Future {
      //block until exit
      val exitCode: Int = processData.nativeProcess.!(
        SProcessLogger(standardStreamWriters.writeStdout,
          standardStreamWriters.writeStderr))

      val stdout = standardStreamWriters.stdoutWriter.toString
      val stderr = standardStreamWriters.stderrWriter.toString

      closeAllStreams(processData)

      new ProgramOutput(exitCode, stdout, stderr)
    }
  }
}

object Process {
  type NativeProcessType = scala.sys.process.ProcessBuilder

  def processWithStandardStreams(processData: ProcessData): Process = {
    val standardStreamWriters = new StandardStreamWriters()
    new Process(
      processData.addStreamWriters(standardStreamWriters.writerMap),
      standardStreamWriters)
  }
}
