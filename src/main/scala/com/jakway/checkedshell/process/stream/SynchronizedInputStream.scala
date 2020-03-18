package com.jakway.checkedshell.process.stream

import java.io.InputStream

class SynchronizedInputStream(val lock: Object,
                              private val is: InputStream,
                              val afterClose: () => Unit)
  extends InputStream with SynchronizedStream {

  override def available(): Int = lock.synchronized(is.available())

  override def mark(i: Int): Unit = lock.synchronized(is.mark(i))

  override def markSupported(): Boolean = lock.synchronized(is.markSupported())

  override def read(): Int = lock.synchronized(is.read())

  override def read(bytes: Array[Byte]): Int =
    lock.synchronized(is.read(bytes))

  override def read(bytes: Array[Byte], i: Int, i1: Int): Int =
    lock.synchronized(is.read(bytes, i, i1))

  override def reset(): Unit = lock.synchronized(is.reset())

  override def skip(l: Long): Long = lock.synchronized(is.skip(l))

  override def close(): Unit = {
    lock.synchronized {
      is.close()
      afterClose()
    }
  }

  override def finalize(): Unit = lock.synchronized(is.finalize())
}
