package com.jakway.checkedshell.error.checks

trait TaggedErrorChecks[A <: ErrorCheck] {
  def checks: Seq[A]
  def tag: CheckMode
}

case class TaggedOutputChecks(checks: Seq[OutputCheck], tag: CheckMode)
  extends TaggedErrorChecks[OutputCheck]