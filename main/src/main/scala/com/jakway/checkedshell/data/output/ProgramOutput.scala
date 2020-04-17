package com.jakway.checkedshell.data.output

import java.io.{InputStream, OutputStream}

import com.jakway.checkedshell.data.output.ProgramOutput.Accumulator
import com.jakway.checkedshell.process.stream.pipes.input.InputWrapper

import scala.concurrent.{ExecutionContext, Future}

trait ProgramOutput {
  val futureExitCode: Future[Int]
  val pipedStdout: InputWrapper
  val pipedStderr: InputWrapper
  val accumulator: ProgramOutput.Accumulator

  def withAccumulator(accumulator: Accumulator): ProgramOutput

  def toFuture(implicit ec: ExecutionContext): Future[FinishedProgramOutput] = {
    for {
      stdoutFromFuture <- pipedStdout.getInputAsFutureString()(ec)
      stderrFromFuture <- pipedStderr.getInputAsFutureString()(ec)
      exitCodeFromFuture <- futureExitCode
    } yield {
      new FinishedProgramOutput(
        exitCodeFromFuture,
        stdoutFromFuture,
        stderrFromFuture,
        accumulator)
    }
  }
}

object ProgramOutput {
  val empty: ProgramOutput =
    new FinishedProgramOutput(0, "", "", Accumulator.empty)

  case class Accumulator(threads: Set[Thread],
    inputStreams: Set[InputStream],
    outputStreams: Set[OutputStream]) {

    def addThreads(xs: Set[Thread]): Accumulator =
      copy(threads = this.threads ++ xs)

    def addInputStreams(xs: Set[InputStream]): Accumulator =
      copy(inputStreams = this.inputStreams ++ xs)

    def addOutputStreams(xs: Set[OutputStream]): Accumulator =
      copy(outputStreams = this.outputStreams ++ xs)
  }

  object Accumulator {
    val empty: Accumulator = Accumulator(Set.empty, Set.empty, Set.empty)
  }
}
