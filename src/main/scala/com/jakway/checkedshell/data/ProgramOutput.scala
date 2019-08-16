package com.jakway.checkedshell.data

class ProgramOutput(val exitCode: Int,
                    val stdout: String,
                    val stderr: String)

object ProgramOutput {
  def unapply(x: ProgramOutput): Option[(Int, String, String)] = {
    Some((x.exitCode, x.stdout, x.stderr))
  }
}
