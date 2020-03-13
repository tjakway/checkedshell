package com.jakway.checkedshell.process.stream.pipes

import java.io.{BufferedReader, InputStream, InputStreamReader, Reader, StringReader}
import java.nio.charset.Charset

import com.jakway.checkedshell.util.StringReaderUtil

import scala.io.Source

trait InputWrapper {
  val encoding: String

  def toReader(enc: String = encoding): Reader = {
    new BufferedReader(
      new InputStreamReader(toInputStream, enc))
  }

  def toReader(charset: Charset): Reader =
    toReader(charset.displayName())

  def toInputStream: InputStream

  def getInputAsString(enc: String = encoding): String =
    StringReaderUtil.inputStreamToString(toInputStream, enc)

  def toLines(enc: String = encoding): Iterator[String] = {
    Source
      .fromInputStream(toInputStream, enc)
      .getLines
  }
}

object InputWrapper {
  private class InputStreamInputWrapper(val is: InputStream,
                                        val encoding: String)
    extends InputWrapper {
    override def toInputStream: InputStream = is
  }

  def apply(is: InputStream, encoding: String): InputWrapper =
    new InputStreamInputWrapper(is, encoding)
}