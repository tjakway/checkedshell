package com.jakway.checkedshell.process.stream.multiplex

import java.io.{InputStream, OutputStream}

class MultiplexInputStream(val from: InputStream,
                           val subStreams: Seq[OutputStream],
                           val closeSubStreams: Boolean =
                             MultiplexInputStream.defaultCloseSubstreams,
                           val afterClose: () => Unit =
                            MultiplexIOStream.defaultAfterClose)
  extends InputStream
    with MultiplexIOStream {

  /**
   * shorthand to pass results from [[from]] to
   * [[subStreams]] before returning them
   * @param f
   * @param g
   * @tparam A
   * @return
   */
  private def withResult[A](f: InputStream => A)
                           (g: A => Unit): A = {
    val res = f(from)
    g(res)
    res
  }

  override def available(): Int = from.available()

  override def mark(i: Int): Unit = from.mark(i)

  override def markSupported(): Boolean = from.markSupported()

  override def read(): Int = {
    withResult(_.read())(x =>
      subStreams.foreach(_.write(x)))
  }

  override def read(bytes: Array[Byte]): Int =
    withResult(_.read(bytes))(ignored =>
      subStreams.foreach(_.write(bytes)))

  override def read(bytes: Array[Byte], i: Int, i1: Int): Int =
    withResult(_.read(bytes, i, i1))(ignored =>
      subStreams.foreach(_.write(bytes, i, i1)))

  override def reset(): Unit = from.reset()

  override def skip(l: Long): Long = from.skip(l)

  override def close(): Unit = {
    from.close()

    if(closeSubStreams) {
      subStreams.foreach(_.close())
    }

    afterClose()
  }
}

object MultiplexInputStream {
  val defaultCloseSubstreams: Boolean = true
}
