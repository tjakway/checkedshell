package com.jakway.checkedshell.data.output

import com.jakway.checkedshell.config.Config
import com.jakway.checkedshell.process.stream.pipes.input.InputWrapper

import scala.concurrent.Future

class FinishedProgramOutput(val exitCode: Int,
                            val stdout: String,
                            val stderr: String)
  extends InProcessProgramOutput(
    Future.successful(exitCode),
    InputWrapper(stdout, FinishedProgramOutput.inputWrapperConversionEncoding),
    InputWrapper(stderr, FinishedProgramOutput.inputWrapperConversionEncoding)
  ) {

  override def toString: String = {
    //workaround for scala not handling quotes in interpolated strings
    //see https://github.com/scala/bug/issues/6476
    new java.util.Formatter()
      .format("FinishedProgramOutput(%s, \"%s\", \"%s\")",
        exitCode.toString, stdout, stderr)
      .toString
  }

  override def equals(obj: Any): Boolean = obj match {
    case FinishedProgramOutput(otherExitCode, otherStdout, otherStderr) => {
      exitCode == otherExitCode &&
        stdout == otherStdout &&
        stderr == otherStderr
    }
  }
}

object FinishedProgramOutput {
  /**
   * encoding shouldn't matter when representing a FinishedProgramOutput
   * as an InProcessProgramOutput because we don't actually have to read
   * anything from an InputStream, we just have to pretend that we did
   */
  private val inputWrapperConversionEncoding: String =
    Config.defaultEncoding

  def unapply(x: FinishedProgramOutput): Option[(Int, String, String)] = {
    Some((x.exitCode, x.stdout, x.stderr))
  }
}

