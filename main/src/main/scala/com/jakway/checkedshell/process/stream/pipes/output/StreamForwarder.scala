package com.jakway.checkedshell.process.stream.pipes.output

import java.io.{InputStream, OutputStream}
import java.util.concurrent.atomic.AtomicBoolean

import com.jakway.checkedshell.error.CheckedShellException
import com.jakway.checkedshell.error.behavior.LogError
import org.slf4j.{Logger, LoggerFactory}

import scala.annotation.tailrec
import scala.util.Try

class StreamForwarder(val in: InputStream,
                      val out: OutputStream,
                      val onExit: () => Unit =
                        StreamForwarder.doNothingOnExit) extends Thread {
  private val logger: Logger = LoggerFactory.getLogger(getClass)
  private val killFlag: AtomicBoolean = new AtomicBoolean(false)
  private val buf: Array[Byte] = new Array(StreamForwarder.bufSize)

  def kill(): Unit = killFlag.set(true)

  private var calledOnExit: Boolean = false
  private def tryOnExit(): Unit = {
    calledOnExit = true
    if(calledOnExit) {
      //do nothing
      logger.debug("Already ran onExit, ignoring second call")
    } else {
      onExit()
    }
  }

  override def run(): Unit = {
    @tailrec
    def helper(): Unit = {
      if(killFlag.get) {
        logger.debug("Kill flag set, exiting")
      } else {
        val amountResult = in.read(buf)

        if(amountResult < 0) {
          logger.debug("Stream closed, quitting")

          //sanity check
        } else if(amountResult == 0) {
          throw StreamForwarder.ZeroLengthBufferException

        } else {
          out.write(buf, 0, amountResult)
          helper()
        }
      }
    }

    try {
      helper()
    } finally {
      //dont throw an exception in finally
      LogError.logIfFailure(logger)(Try(onExit()))
    }
  }
}

object StreamForwarder {
  val bufSize: Int = 4096

  def doNothingOnExit: () => Unit = {
    def f(): Unit = {}
    f
  }

  class InputWrapperForwarderException(override val msg: String)
    extends CheckedShellException(msg)

  object ZeroLengthBufferException
    extends CheckedShellException("InputStream.read returned 0, " +
      "indicating underlying buffer had a length of 0 (should not happen)")
}
