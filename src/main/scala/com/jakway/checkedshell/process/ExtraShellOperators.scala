package com.jakway.checkedshell.process

trait ExtraShellOperators[A] {
  def andPipe(snd: A): A
  def orPipe(snd: A): A
}
