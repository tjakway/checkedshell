package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.StreamWriters
import com.jakway.checkedshell.process.Job.{JobInput, RunJobF}
import com.jakway.checkedshell.process.stream.StandardStreamWriters
import com.jakway.checkedshell.process.stream.pipes.output.OutputWrapper.{StderrWrapper, StdoutWrapper}

import scala.concurrent.{ExecutionContext, Future}

class FunctionJob(val body: RunJobF,
                  val streamWriters: StreamWriters =
                    FunctionJob.defaultStreamWriters,
                  val optDescription: Option[String]) extends Job {

  override protected def runJob(input: JobInput)
                      (stdoutWrapper: StdoutWrapper)
                      (stderrWrapper: StderrWrapper)
                      (implicit rc: RunConfiguration,
                                ec: ExecutionContext): Future[Int] = {
    body(input)(stdoutWrapper)(stderrWrapper)(rc)(ec)
  }

  override protected def getStreamWriters: StreamWriters = streamWriters

  override protected def copyWithStreamWriters(
    newStreamWriters: StreamWriters): Job =
    new FunctionJob(body, streamWriters, optDescription)
}

object FunctionJob {
  def defaultStreamWriters: StreamWriters =
    new StandardStreamWriters().streamWriters

  val returnSuccessJob: FunctionJob = {
    def returnSuccessF: RunJobF =
      (input: JobInput) =>
      (stdoutWrapper: StdoutWrapper) =>
      (stderrWrapper: StderrWrapper) =>
      (rc: RunConfiguration) =>
      (ec: ExecutionContext) => Future.successful(0)

    new FunctionJob(returnSuccessF,
      defaultStreamWriters,
      Some("This job does nothing but return success (0)"))
  }
}
