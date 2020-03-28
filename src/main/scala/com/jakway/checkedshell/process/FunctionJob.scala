package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.StreamWriters
import com.jakway.checkedshell.process.Job.{JobInput, RunJobF}
import com.jakway.checkedshell.process.stream.pipes.output.OutputWrapper.{StderrWrapper, StdoutWrapper}

import scala.concurrent.{ExecutionContext, Future}

class FunctionJob(val body: RunJobF,
                  val streamWriters: StreamWriters,
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
