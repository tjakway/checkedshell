package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.error.cause.ErrorCause
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.{ExecutionContext, Future}

trait OutputCheck {
  /**
   * Whether this check requires us to
   * wait for the checked process to terminate
   * before piping its output to the next process
   */
  val forcesWait: Boolean

  def checkOutput(output: JobOutput)
                 (implicit rc: RunConfiguration,
                           ec: ExecutionContext): Future[Set[ErrorCause]]
}
