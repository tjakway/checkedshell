package com.jakway.checkedshell.config

import com.jakway.checkedshell.error.behavior.{ErrorBehavior, ThrowOnError}

case class RunConfiguration(errorBehavior: ErrorBehavior)

object RunConfiguration {
  val default: RunConfiguration = RunConfiguration(ThrowOnError)
}