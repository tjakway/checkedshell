package com.jakway.checkedshell.config

import java.nio.charset.{Charset, StandardCharsets}

import com.jakway.checkedshell.error.behavior.{ErrorBehavior, ThrowOnError}

/**
 *
 * @param ignoreCloseStreamErrors
 * @param closeStreamsAfterExit whether write streams should be closed when a process exits
 * @param closeStreamsOnReplacement whether replaced streams should be explicitly closed()
 *                                  or just have their references reassigned
 * @param closeOtherStreamsOnRedirect whether redirecting closes other streams for
 *                                    that descriptor
 */
case class StreamsConfiguration(ignoreCloseStreamErrors: Boolean,
                                closeStreamsAfterExit: Boolean,
                                closeStreamsOnReplacement: Boolean,
                                closeOtherStreamsOnRedirect: Boolean)

object StreamsConfiguration {
  val default: StreamsConfiguration =
    StreamsConfiguration(
      ignoreCloseStreamErrors = false,
      closeStreamsAfterExit = true,
      closeStreamsOnReplacement = true,
      closeOtherStreamsOnRedirect = true)
}

/**
 *
 * @param errorBehavior
 * @param charset
 */
case class RunConfiguration(errorBehavior: ErrorBehavior,
                            charset: Charset,
                            streamsConfiguration: StreamsConfiguration)

object RunConfiguration {
  val default: RunConfiguration = RunConfiguration(
    ThrowOnError,
    StandardCharsets.UTF_8,
    StreamsConfiguration.default)
}