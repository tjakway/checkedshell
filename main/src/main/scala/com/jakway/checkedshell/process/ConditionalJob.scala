package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.StreamWriters
import com.jakway.checkedshell.data.output.ProgramOutput
import com.jakway.checkedshell.process.Job.{ExecJobF, JobInput}

import scala.concurrent.{ExecutionContext, Future}

class ConditionalJob(val condition: Future[Boolean],
                     val ifTrue: ExecJobF,
                     val ifFalse: ExecJobF,
                     val pipe: Boolean,
                     override val streamWriters: StreamWriters,
                     override val optDescription: Option[String] = None)
  extends MultiStepJob(
    ConditionalJob.choose(condition, ifTrue, ifFalse, pipe),
    streamWriters, optDescription)

object ConditionalJob {
  private def choose(futureCondition: Future[Boolean],
                     ifTrue: ExecJobF,
                     ifFalse: ExecJobF,
                     pipe: Boolean): ExecJobF = {
    (input: JobInput) => (rc: RunConfiguration) => (ec: ExecutionContext) => {

      def passInput: JobInput = {
        if(pipe) {
          input
        } else {
          None
        }
      }

      futureCondition.flatMap { condition =>
        if(condition) {
          ifTrue(passInput)(rc)(ec)
        } else {
          ifFalse(passInput)(rc)(ec)
        }
      }(ec)
    }
  }
}
