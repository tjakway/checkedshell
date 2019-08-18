package com.jakway.checkedshell.process

import java.io.{InputStream, StringReader}

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.{ProcessData, ProgramOutput}
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.process.stream.StandardStreamWriters
import org.apache.commons.io.input.ReaderInputStream

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process.{ProcessLogger => SProcessLogger, ProcessBuilder => SProcessBuilder}

/**
 * a job that runs as an external process
 *
 * @param standardStreamWriters need to take this as a parameter so we
 *                              know which writers in processData.streamWriters
 *                              to extract stdout and stderr from
 */
class Process(val processData: ProcessData,
              private val standardStreamWriters: StandardStreamWriters)
  extends Job
    with HasProcessData[Process] {

  override def copyWithProcessData(newProcessData: ProcessData): Process =
    new Process(newProcessData, standardStreamWriters)

  private def programOutputToInputStream(output: ProgramOutput)
                                        (implicit rc: RunConfiguration): InputStream = {

    new ReaderInputStream(new StringReader(output.stdout), rc.charset)
  }

  /**
   * TODO: it would be better to build pipes more faithfully to the unix ideal:
   * by connecting processes via streams instead of waiting until one process is done
   * then vacuuming the entire output into a string, which is both less efficient
   * and not a replacement for the intended behavior
   * @param input
   * @param rc
   * @param ec
   * @return
   */
  override protected def runJob(input: Option[ProgramOutput])
                               (implicit rc: RunConfiguration,
                                         ec: ExecutionContext): JobOutput = {
    Future {
      //connect stdin if available
      val procWithConnectedInput: SProcessBuilder = {
        input match {
          case Some(i) => {
            processData.nativeProcess #< programOutputToInputStream(i)
          }
          case None => processData.nativeProcess
        }
      }

      //block until exit
      val exitCode: Int = procWithConnectedInput.!(
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

  //TODO: add more factory methods (apply)
}
