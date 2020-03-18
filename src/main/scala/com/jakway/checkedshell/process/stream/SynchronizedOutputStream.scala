package com.jakway.checkedshell.process.stream

import java.io.OutputStream

class SynchronizedOutputStream(val lock: Object,
                               private val os: OutputStream,
                               val afterClose: () => Unit)
  extends OutputStream with SynchronizedStream {
  override def write(i: Int): Unit = lock.synchronized(os.write(i))
  override def write(bytes: Array[Byte]): Unit =
    lock.synchronized(os.write(bytes))

  override def write(bytes: Array[Byte], i: Int, i1: Int): Unit =
    lock.synchronized(os.write(bytes, i, i1))

  override def flush(): Unit = lock.synchronized(os.flush())

  override def close(): Unit = {
    lock.synchronized {
      os.close()
      afterClose()
    }
  }

  override def finalize(): Unit = lock.synchronized(os.finalize())
}
