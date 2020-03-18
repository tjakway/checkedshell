package com.jakway.checkedshell.data.output

import com.jakway.checkedshell.process.stream.pipes.input.InputWrapper

import scala.concurrent.{ExecutionContext, Future}

trait ProgramOutput {
  val futureExitCode: Future[Int]
  val pipedStdout: InputWrapper
  val pipedStderr: InputWrapper

  def toFuture(implicit ec: ExecutionContext): Future[FinishedProgramOutput] = {
    for {
      stdoutFromFuture <- pipedStdout.getInputAsFutureString()(ec)
      stderrFromFuture <- pipedStderr.getInputAsFutureString()(ec)
      exitCodeFromFuture <- futureExitCode
    } yield {
      new FinishedProgramOutput(
        exitCodeFromFuture,
        stdoutFromFuture,
        stderrFromFuture)
    }
  }
}
