package com.jakway.checkedshell.process

trait ShellOperators[A] {
  def `;`(snd: A): A = sequence(snd)
  def `&&`(snd: A): A = and(snd)
  def `||`(snd: A): A = or(snd)

  def sequence(snd: A): A
  def and(snd: A): A
  def or(snd: A): A
}
