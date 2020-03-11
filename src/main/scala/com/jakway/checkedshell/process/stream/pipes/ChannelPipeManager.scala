package com.jakway.checkedshell.process.stream.pipes
import java.io.{InputStream, OutputStream}
import java.nio.channels.{Channels, Pipe}
import java.util.Formatter

import com.jakway.checkedshell.error.behavior.CloseBehavior
import com.jakway.checkedshell.error.behavior.CloseBehavior.CloseReturnType
import com.jakway.checkedshell.error.cause
import com.jakway.checkedshell.error.cause.CloseStreamError
import org.slf4j.{Logger, LoggerFactory}

private class ChannelPipeManager(val pipe: ChannelPipeManager.PipeType,
                                 val desc: Option[String])
  extends PipeManager {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  protected val is: InputStream = Channels.newInputStream(pipe.source())
  protected val os: OutputStream = Channels.newOutputStream(pipe.sink())

  private var inputStreamClosed: Boolean = false
  private var outputStreamClosed: Boolean = false

  private def checkDoubleClose(streamDesc: String)
                              (closed: Boolean,
                               closeBehavior: CloseBehavior):
    CloseBehavior.CloseReturnType = {

    if(closeBehavior.alwaysReturnSuccess) {
      Right({})
    } else {
      if(closed && !closeBehavior.allowDoubleClose) {
        Left(new cause.CloseStreamError.DoubleCloseError(
          s"Tried to double close $streamDesc end of pipe in "
            + toString))
      } else {
        Right({})
      }
    }
  }


  protected def tryCloseChannels(
        implicit closeBehavior: CloseBehavior): CloseReturnType = {
    synchronized {
      if(inputStreamClosed && outputStreamClosed) {
        logger.debug("Closing channel...")
        val res = for {
          _ <- CloseBehavior.wrapCloseCall(pipe.source())
          _ <- CloseBehavior.wrapCloseCall(pipe.sink())
        } yield {}

        res match {
          case Right(_) =>
            logger.debug("Channel closed.")
          case Left(_) => logger.debug("Failed to close channel.")
        }

        res
      } else {
        Right({})
      }
    }
  }

  override def getInputStream: InputStream = is
  override def getOutputStream: OutputStream = os

  //TODO: may want to refactor each stream's fields into a separate class
  //then abstract this method over that class
  override def closeInputStream(implicit closeBehavior: CloseBehavior): CloseReturnType = {
    synchronized {
      val res = for {
        _ <- checkDoubleClose("InputStream")(
          inputStreamClosed, closeBehavior)
        _ <- CloseBehavior.wrapCloseCall(is)
        _ <- tryCloseChannels
      } yield {
        //only reached if everything is successful up to this point
        inputStreamClosed = true
      }

      closeBehavior.throwIfIndicated(res)

    }
  }
  override def closeOutputStream(implicit closeBehavior: CloseBehavior): CloseReturnType = {
    synchronized {
      val res = for {
        _ <- checkDoubleClose("OutputStream")(
          outputStreamClosed, closeBehavior)
        _ <- CloseBehavior.wrapCloseCall(os)
        _ <- tryCloseChannels
      } yield {
        //only reached if everything is successful up to this point
        outputStreamClosed = true
      }

      closeBehavior.throwIfIndicated(res)
    }
  }

  override def closeAll(implicit closeBehavior: CloseBehavior): CloseReturnType = {
    closeInputStream
    closeOutputStream
  }

  override protected def getDefaultCloseBehavior: CloseBehavior =
    CloseBehavior.default()

  override def toString: String = {
    val fmt: Formatter = {
      val sb = new StringBuffer()
      new Formatter(sb)
    }
    fmt.format("%s(%s)",
      getClass.getSimpleName,
      desc.getOrElse("<No description>"))
    fmt.toString
  }
}

object ChannelPipeManager {
  type PipeType = java.nio.channels.Pipe
}
