package com.jakway.checkedshell.data

import com.jakway.checkedshell.data.output.FinishedProgramOutput

class TypedProgramOutput[+A](override val exitCode: Int,
                             val data: A,
                             override val stderr: String,
                             override val stdout: String)
  extends FinishedProgramOutput(exitCode, stdout, stderr) {

  def this(exitCode: Int, data: A, stderr: String) {
    this(exitCode, data, stderr, data.toString)
  }
}

object TypedProgramOutput {
  def unapply[A](x: TypedProgramOutput[A]): Option[(Int, A, String)] = {
    Some((x.exitCode, x.data, x.stderr))
  }
}

