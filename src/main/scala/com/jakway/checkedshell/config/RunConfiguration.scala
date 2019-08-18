package com.jakway.checkedshell.config

import com.jakway.checkedshell.error.behavior.{ErrorBehavior, ThrowOnError}

case class RunConfiguration(errorBehavior: ErrorBehavior,
                            ignoreCloseStreamErrors: Boolean)

object RunConfiguration {
  val default: RunConfiguration = RunConfiguration(
    ThrowOnError,
    ignoreCloseStreamErrors = false)
}