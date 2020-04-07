package com.jakway.checkedshell.process.stream.multiplex

import java.io.OutputStream

class MultiplexOutputStream(val subStreams: Seq[OutputStream],
                            val afterClose: () => Unit =
                              MultiplexIOStream.defaultAfterClose)
  extends OutputStream
    with MultiplexIOStream {

  override def write(i: Int): Unit =
    subStreams.foreach(_.write(i))

  override def write(bytes: Array[Byte]): Unit =
    subStreams.foreach(_.write(bytes))

  override def write(bytes: Array[Byte], i: Int, i1: Int): Unit =
    subStreams.foreach(_.write(bytes, i, i1))

  override def flush(): Unit = subStreams.foreach(_.flush())

  override def close(): Unit =
    subStreams.foreach(_.close())
}
