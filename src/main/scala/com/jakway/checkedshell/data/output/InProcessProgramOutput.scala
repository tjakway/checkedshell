package com.jakway.checkedshell.data.output

import com.jakway.checkedshell.process.stream.pipes.InputWrapper

import scala.concurrent.{ExecutionContext, Future}

class InProcessProgramOutput(val futureExitCode: Future[Int],
                             val pipedStdout: InputWrapper,
                             val pipedStderr: InputWrapper) {

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
