package com.jakway.checkedshell.process.stream.multiplex

trait MultiplexIOStream {
  def afterClose: () => Unit
}

object MultiplexIOStream {
  val defaultAfterClose: () => Unit = {
    def doNothing(): Unit = {}
    doNothing
  }
}
