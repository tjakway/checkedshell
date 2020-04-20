package com.jakway.checkedshell.data.output

import java.io.{InputStream, OutputStream}

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.output.ProgramOutput.Accumulator
import com.jakway.checkedshell.error.CheckedShellException
import com.jakway.checkedshell.process.stream.pipes.input.InputWrapper

import scala.concurrent.duration.Duration
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
    outputStreams: Set[OutputStream],
    cleanupFuture: Option[Future[Unit]]) {

    def addThreads(xs: Set[Thread]): Accumulator =
      copy(threads = this.threads ++ xs)

    def addInputStreams(xs: Set[InputStream]): Accumulator =
      copy(inputStreams = this.inputStreams ++ xs)

    def addOutputStreams(xs: Set[OutputStream]): Accumulator =
      copy(outputStreams = this.outputStreams ++ xs)

    def withCleanupFuture(newCleanupFuture: Future[Unit]): Accumulator =
      copy(cleanupFuture = newCleanupFuture)
  }

  object Accumulator {
    val empty: Accumulator =
      Accumulator(Set.empty, Set.empty, Set.empty, None)
  }


  object AwaitErrorChecks {
    case class AwaitErrorChecksException(override val msg: String)
      extends CheckedShellException(msg)

    private def joinThread(t: Thread, waitDuration: Duration): Unit = {
      if(waitDuration.isFinite()) {
        val millis = waitDuration.toMillis
        if(millis >= 0) {
          t.join(millis)
        } else {
          throw AwaitErrorChecksException(
            s"Could not wait $waitDuration for Thread $t " +
              s"because waitDuration.millis < 0")
        }
      } else {
        t.join()
      }
    }

    private def awaitThreads
      (programOutput: ProgramOutput)
      (implicit ec: ExecutionContext,
        rc: RunConfiguration):
      Future[Unit] = Future {
          programOutput
            .accumulator
            .threads
            .foreach(joinThread(_, rc.errorConfiguration.awaitJobTimeout))
        }

    private def closeStreams
      (programOutput: ProgramOutput)
      (implicit ec: ExecutionContext): Future[Unit] = Future {
        programOutput.accumulator.inputStreams.foreach(_.close())
        programOutput.accumulator.outputStreams.foreach(_.close())
    }

    def apply(programOutput: ProgramOutput)
      (implicit ec: ExecutionContext,
                rc: RunConfiguration): Future[ProgramOutput] = {
      for {
        _ <- awaitThreads(programOutput)
        _ <- closeStreams(programOutput)
      } yield {
        programOutput
      }
    }
  }

}
