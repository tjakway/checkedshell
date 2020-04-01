package com.jakway.checkedshell.process.stream.pipes
import java.io.{InputStream, OutputStream}
import java.nio.channels.{Channels, Pipe}
import java.util.Formatter

import com.jakway.checkedshell.error.behavior.CloseBehavior
import com.jakway.checkedshell.error.behavior.CloseBehavior.CloseReturnType
import com.jakway.checkedshell.error.cause
import com.jakway.checkedshell.process.stream.{SynchronizedInputStream, SynchronizedOutputStream}
import com.jakway.checkedshell.process.stream.pipes.input.InputWrapper
import com.jakway.checkedshell.process.stream.pipes.output.OutputWrapper
import org.slf4j.{Logger, LoggerFactory}

private class ChannelPipeManager(val pipe: ChannelPipeManager.PipeType,
                                 val optDescription: Option[String],
                                 val defaultCloseBehavior: CloseBehavior)
  extends PipeManager {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  protected val is: InputStream = Channels.newInputStream(pipe.source())
  protected val os: OutputStream = Channels.newOutputStream(pipe.sink())

  private var inputStreamClosed: Boolean = false
  private var outputStreamClosed: Boolean = false

  override protected def getDefaultCloseBehavior: CloseBehavior =
    defaultCloseBehavior

  override def getInputWrapper(enc: String): InputWrapper = {
    InputWrapper(
      new SynchronizedInputStream(
        this, getInputStream,
        () => closeInputStream(getDefaultCloseBehavior)),
      enc, inputWrapperDescription
    )
  }

  override def getOutputWrapper(enc: String): OutputWrapper = {
    OutputWrapper(
      new SynchronizedOutputStream(
        this, getOutputStream,
        () => closeOutputStream(getDefaultCloseBehavior)),
      enc, outputWrapperDescription
    )
  }

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

  override def toString: String = {
    val fmt: Formatter = {
      val sb = new StringBuffer()
      new Formatter(sb)
    }
    fmt.format("%s(%s)",
      getClass.getSimpleName,
      optDescription.getOrElse("<No description>"))
    fmt.toString
  }

  protected def wrapperDescription(optDescription: Option[String],
                                   wrapperType: String): String = {
    wrapperType + " of " + optDescription
  }
  protected def inputWrapperDescription: String =
    wrapperDescription(optDescription, classOf[InputWrapper].getSimpleName)
  protected def outputWrapperDescription: String =
    wrapperDescription(optDescription, classOf[OutputWrapper].getSimpleName)

}

object ChannelPipeManager extends WithPipeManagerConstructors {
  type PipeType = java.nio.channels.Pipe
}