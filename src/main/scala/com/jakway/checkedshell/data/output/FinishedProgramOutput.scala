package com.jakway.checkedshell.data.output

class FinishedProgramOutput(val exitCode: Int,
                            val stdout: String,
                            val stderr: String) {
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
  def unapply(x: FinishedProgramOutput): Option[(Int, String, String)] = {
    Some((x.exitCode, x.stdout, x.stderr))
  }
}
