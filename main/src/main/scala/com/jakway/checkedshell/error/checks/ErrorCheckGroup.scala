package com.jakway.checkedshell.error.checks

trait ErrorCheckGroup[A] {
  def allChecks: Seq[A]

  def optDescription: Option[String]
}
