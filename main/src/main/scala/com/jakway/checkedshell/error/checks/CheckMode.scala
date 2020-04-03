package com.jakway.checkedshell.error.checks

sealed abstract class CheckMode

object CheckMode {
  case object HandleImmediately extends CheckMode

  case class Aggregate(forceWait: Boolean) extends CheckMode
}

