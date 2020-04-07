package com.jakway.checkedshell.process.stream.multiplex

trait MultiplexIOStream[A] {
  val subStreams: Seq[A]
}


