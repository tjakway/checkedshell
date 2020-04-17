package com.jakway.checkedshell.data.output

import com.jakway.checkedshell.data.output.ProgramOutput.Accumulator
import com.jakway.checkedshell.process.stream.pipes.input.InputWrapper

import scala.concurrent.Future

class InProgressProgramOutput(val futureExitCode: Future[Int],
                             val pipedStdout: InputWrapper,
                             val pipedStderr: InputWrapper,
                             override val accumulator: Accumulator)
  extends ProgramOutput {

  override def withAccumulator(accumulator: Accumulator): ProgramOutput =
    new InProgressProgramOutput(futureExitCode, pipedStdout, pipedStderr, accumulator)
}
