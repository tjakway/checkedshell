package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.output.FinishedProgramOutput
import com.jakway.checkedshell.error.cause.ErrorCause
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.{ExecutionContext, Future}

trait FinishedOutputCheck
  extends OutputCheck {
  override val forcesWait: Boolean = true

  override def checkOutput(output: JobOutput)
                          (implicit rc: RunConfiguration,
                                    ec: ExecutionContext):
    Future[Set[ErrorCause]] = {
    output.flatMap(_.toFuture).map(checkFinishedOutput)
  }

  protected def checkFinishedOutput(
               output: FinishedProgramOutput)
                                   (implicit rc: RunConfiguration): Set[ErrorCause]
}

object FinishedOutputCheck {
  type GetOutput = FinishedProgramOutput => String
}
