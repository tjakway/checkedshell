package com.jakway.checkedshell.process.stream.pipes.output

import java.io.OutputStream
import java.util

import com.jakway.checkedshell.process.stream.pipes.output.PacketOutputStream.PacketType

abstract class PacketOutputStream extends OutputStream {
  val copyPackets: Boolean

  protected def forward(packet: PacketType): Unit

  override def write(bytes: Array[Byte]): Unit = {
    val x = {
      if(copyPackets) {
        util.Arrays.copyOf(bytes, bytes.length)
      } else {
        bytes
      }
    }
    forward(x)
  }

  override def write(i: Int): Unit = write(Array(i.byteValue()))

  override def write(bytes: Array[Byte], off: Int, len: Int): Unit = {
    write(util.Arrays.copyOfRange(bytes, off, len))
  }

  override def flush(): Unit = {}
}

object PacketOutputStream {
  type PacketType = Array[Byte]
  val defaultCopyPackets: Boolean = true
}
