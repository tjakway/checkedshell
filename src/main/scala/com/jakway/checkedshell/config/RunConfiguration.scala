package com.jakway.checkedshell.config

import java.nio.charset.{Charset, StandardCharsets}

import com.jakway.checkedshell.error.behavior.{ErrorBehavior, ThrowOnError}

case class RunConfiguration(errorBehavior: ErrorBehavior,
                            charset: Charset,
                            ignoreCloseStreamErrors: Boolean)

object RunConfiguration {
  val default: RunConfiguration = RunConfiguration(
    ThrowOnError,
    StandardCharsets.UTF_8,
    ignoreCloseStreamErrors = false)
}