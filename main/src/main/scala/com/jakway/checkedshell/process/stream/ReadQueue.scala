package com.jakway.checkedshell.process.stream

import java.io.{Closeable, OutputStream}
import java.util
import java.util.{AbstractQueue => JAbstractQueue}
import java.util.concurrent.{ConcurrentLinkedQueue => JConcurrentLinkedQueue}
import java.util.concurrent.atomic.AtomicBoolean

import com.jakway.checkedshell.process.stream.ReadQueue.{BaseType, EntryType, PayloadType, ReadQueueEvent, ReadQueueThread}
import org.slf4j.{Logger, LoggerFactory}

class ReadQueue private (val writeTo: OutputStream) extends Closeable {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private val queue: BaseType =
    new JConcurrentLinkedQueue[EntryType]()

  private val killFlag: AtomicBoolean = new AtomicBoolean(false)
  private lazy val thread: ReadQueueThread =
    new ReadQueueThread(this, writeTo, killFlag)

  private def start(): Unit = thread.start()

  def add(x: PayloadType): Unit = {
    queue.add(ReadQueueEvent.Continue(x))
  }

  override def close(): Unit = {
    logger.debug("Setting kill flag in " + thread.getClass.getName)
    thread.kill()
  }
}

object ReadQueue {
  type EntryType = ReadQueueEvent
  type PayloadType = Array[Byte]
  type BaseType = JAbstractQueue[EntryType]


  sealed abstract class ReadQueueEvent
  object ReadQueueEvent {
    case object Stop extends ReadQueueEvent
    case class Continue(entry: PayloadType) extends ReadQueueEvent
  }

  def apply(writeTo: OutputStream): ReadQueue = {
    val queue = new ReadQueue(writeTo)
    queue.start()
    queue
  }

  private class ReadQueueThread(
    val readQueue: ReadQueue,
    val writeTo: OutputStream,
    val killFlag: AtomicBoolean) extends Thread {
    private val logger: Logger = LoggerFactory.getLogger(getClass)

    def kill(): Unit = killFlag.set(true)

    private def nextEvent: ReadQueueEvent = {
      val killed = killFlag.get()

      if(killed) {
        logger.debug("Kill flag set, thread exiting")
        ReadQueueEvent.Stop
      } else {
        readQueue.queue.remove()
      }
    }

    override def run(): Unit = {
      nextEvent match {
          //TODO: close handling
        case ReadQueueEvent.Stop => {
          logger.debug("Received ReadQueueEvent.Stop, closing OutputStream")
          writeTo.close()
        }

        case ReadQueueEvent.Continue(bytes) => writeTo.write(bytes)
      }
    }
  }
}
