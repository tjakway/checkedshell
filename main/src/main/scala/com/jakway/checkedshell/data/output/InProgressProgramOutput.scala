package com.jakway.checkedshell.data.output

import com.jakway.checkedshell.process.stream.pipes.input.InputWrapper

import scala.concurrent.Future

class InProgressProgramOutput(val futureExitCode: Future[Int],
                             val pipedStdout: InputWrapper,
                             val pipedStderr: InputWrapper)
  extends ProgramOutput
