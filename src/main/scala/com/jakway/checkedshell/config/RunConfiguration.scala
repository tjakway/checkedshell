package com.jakway.checkedshell.config

import java.nio.charset.{Charset, StandardCharsets}

import com.jakway.checkedshell.error.behavior.{ErrorBehavior, ThrowOnError}

/**
 *
 * @param errorBehavior
 * @param charset
 * @param ignoreCloseStreamErrors
 * @param closeStreamsAfterExit whether write streams should be closed when a process exits
 */
case class RunConfiguration(errorBehavior: ErrorBehavior,
                            charset: Charset,
                            ignoreCloseStreamErrors: Boolean,
                            closeStreamsAfterExit: Boolean)

object RunConfiguration {
  val default: RunConfiguration = RunConfiguration(
    ThrowOnError,
    StandardCharsets.UTF_8,
    ignoreCloseStreamErrors = false,
    closeStreamsAfterExit = true)
}