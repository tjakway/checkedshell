package com.jakway.checkedshell.error.checks
import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.error.cause.{ErrorCause, NonzeroExitCode, UnexpectedExitCode}

abstract class ExitCodeCheck(val checkDescription: Option[String])
  extends FinishedOutputCheck {
  override protected def checkFinishedOutput(output: FinishedProgramOutput)
                                            (implicit rc: RunConfiguration): Set[ErrorCause] = {
    if(checkExitCode(output.exitCode)) {
      Set.empty
    } else {
      Set(mkExitCodeErrorCause(output))
    }
  }

  protected def mkExitCodeErrorCause: FinishedProgramOutput =>
    UnexpectedExitCode = new UnexpectedExitCode(checkDescription, _)

  /**
   * @param exitCode
   * @return false in case of error
   */
  protected def checkExitCode(exitCode: Int): Boolean
}

object ExitCodeCheck {
  object ZeroExitCodeCheck extends ExitCodeCheck(None) {
    override protected def mkExitCodeErrorCause: FinishedProgramOutput =>
      UnexpectedExitCode = NonzeroExitCode.apply

    protected def checkExitCode(exitCode: Int): Boolean = exitCode == 0
  }
}