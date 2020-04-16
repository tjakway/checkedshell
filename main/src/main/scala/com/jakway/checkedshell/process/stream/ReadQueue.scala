package com.jakway.checkedshell.process.stream

import java.io.{Closeable, OutputStream}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{ConcurrentLinkedQueue => JConcurrentLinkedQueue}
import java.util.{AbstractQueue => JAbstractQueue}

import com.jakway.checkedshell.process.stream.ReadQueue._
import com.jakway.checkedshell.process.stream.pipes.output.PacketOutputStream
import com.jakway.checkedshell.process.stream.pipes.output.PacketOutputStream.PacketType
import org.slf4j.{Logger, LoggerFactory}

import scala.annotation.tailrec

class ReadQueue private (val writeTo: OutputStream,
                         val copyPackets: Boolean =
                          PacketOutputStream.defaultCopyPackets)
  extends Closeable {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private val queue: BaseType =
    new JConcurrentLinkedQueue[EntryType]()

  private val killFlag: AtomicBoolean = new AtomicBoolean(false)
  private lazy val thread: ReadQueueThread =
    new ReadQueueThread(this, writeTo, killFlag)

  private def start(): Unit = thread.start()
  private lazy val packetStream =
    new ReadQueue.ReadQueuePacketOutputStream(this, copyPackets)

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

  def apply(writeTo: OutputStream,
            copyPackets: Boolean =
              PacketOutputStream.defaultCopyPackets): ReadQueue = {
    val queue = new ReadQueue(writeTo, copyPackets)
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

    @tailrec
    final override def run(): Unit = {
      nextEvent match {
          //TODO: close handling
        case ReadQueueEvent.Stop => {
          logger.debug("Received ReadQueueEvent.Stop, closing OutputStream")
          writeTo.close()
        }

        case ReadQueueEvent.Continue(bytes) => {
          writeTo.write(bytes)
          run()
        }
      }
    }
  }

  private class ReadQueuePacketOutputStream(val readQueue: ReadQueue,
                                            override val copyPackets: Boolean)
    extends PacketOutputStream {
    override protected def forward(packet: PacketType): Unit =
      readQueue.add(packet)
  }
}
