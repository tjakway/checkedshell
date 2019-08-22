package com.jakway.checkedshell.data

class ProgramOutput(val exitCode: Int,
                    val stdout: String,
                    val stderr: String) {
  override def toString: String = {
    //workaround for scala not handling quotes in interpolated strings
    //see https://github.com/scala/bug/issues/6476
    new java.util.Formatter()
      .format("ProgramOutput(%s, \"%s\", \"%s\")",
        exitCode.toString, stdout, stderr)
      .toString
  }

  override def equals(obj: Any): Boolean = obj match {
    case ProgramOutput(otherExitCode, otherStdout, otherStderr) => {
      exitCode == otherExitCode &&
        stdout == otherStdout &&
        stderr == otherStderr
    }
  }
}

object ProgramOutput {
  def unapply(x: ProgramOutput): Option[(Int, String, String)] = {
    Some((x.exitCode, x.stdout, x.stderr))
  }
}
