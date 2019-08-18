package com.jakway.checkedshell.process

import java.io.{InputStream, StringReader}

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.{ProcessData, ProgramOutput, StreamWriters}
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.process.stream.StandardStreamWriters
import org.apache.commons.io.input.ReaderInputStream
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process.{ProcessBuilder => SProcessBuilder}

/**
 * a job that runs as an external process
 */
class Process(val processData: ProcessData,
              val streamWriters: StreamWriters)
  extends Job
    with HasProcessData[Process] {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  override protected def getStreamWriters: StreamWriters = streamWriters
  override protected def copyWithStreamWriters(newStreamWriters: StreamWriters): Process =
    new Process(processData, newStreamWriters)


  override def copyWithProcessData(newProcessData: ProcessData): Process =
    new Process(newProcessData, streamWriters)

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
      val exitCode: Int = procWithConnectedInput.!(streamWriters.toProcessLogger)

      val stdout = streamWriters.stdoutWriter.map(_.toString).getOrElse("")
      val stderr = streamWriters.stderrWriter.map(_.toString).getOrElse("")

      if(rc.closeStreamsAfterExit) {
        closeAllStreams(rc)
        logger.debug("Streams closed")
      }

      new ProgramOutput(exitCode, stdout, stderr)
    }
  }
}

object Process {
  type NativeProcessType = scala.sys.process.ProcessBuilder

  def processWithStandardStreams(processData: ProcessData): Process = {
    val standardStreamWriters = new StandardStreamWriters()
    new Process(
      processData,
      standardStreamWriters.streamWriters)
  }

  //TODO: add more factory methods (apply)
}
