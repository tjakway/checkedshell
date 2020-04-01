package com.jakway.checkedshell.error.checks

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.output.ProgramOutput
import com.jakway.checkedshell.error.cause.ErrorCause
import com.jakway.checkedshell.error.checks.PerLineCheck.GetLines
import com.jakway.checkedshell.process.Job.JobOutput

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait PerLineCheck extends OutputCheck {
  override val forcesWait: Boolean = false
  def stopEarly: Boolean = PerLineCheck.defaultStopEarly

  protected def getLines: GetLines
  protected def checkLine: ProgramOutput => String => Option[ErrorCause]


  override def checkOutput(output: JobOutput)
                          (implicit rc: RunConfiguration,
                                    ec: ExecutionContext):
    Future[Set[ErrorCause]] = {

    //use exceptions for non-local control flow
    output.map { out =>
      val res = Try {
        val empty: Set[ErrorCause] = Set.empty
        getLines(rc.encoding)(out).foldLeft(empty) {
          case (acc, thisLine) => {
            checkLine(out)(thisLine) match {
                //throw if we need to exit early
              case Some(foundError) if stopEarly =>
                throw PerLineCheck.ReturnErrorThrowable(foundError)

              case Some(foundError) => acc + foundError
              case None => acc
            }
          }
        }
      } recover {
        case PerLineCheck.ReturnErrorThrowable(cause) => Set(cause)
      }

      res.get
    }
  }
}

object PerLineCheck {
  val defaultStopEarly: Boolean = true
  type GetLines = String => ProgramOutput => Iterator[String]

  def getStdoutLines: GetLines =
    enc => out => {
    out.pipedStdout.toLines(enc)
  }

  def getStderrLines: GetLines =
    enc => out => {
      out.pipedStderr.toLines(enc)
    }

  private case class ReturnErrorThrowable(errorCause: ErrorCause)
    extends Throwable
}
