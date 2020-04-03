package com.jakway.checkedshell.error.checks

trait TaggedErrorChecks[A <: ErrorCheck] {
  def checks: Seq[A]
  def tag: CheckMode

  def optDescription: Option[String]
}

case class TaggedOutputChecks(checks: Seq[OutputCheck],
  tag: CheckMode,
  optDescription: Option[String])
  extends TaggedErrorChecks[OutputCheck]