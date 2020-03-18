package com.jakway.checkedshell.process.stream.pipes.input

import java.io._
import java.nio.charset.Charset

import com.jakway.checkedshell.util.StringReaderUtil
import org.apache.commons.io.input.ReaderInputStream

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

trait InputWrapper {
  val encoding: String

  def getReader(enc: String = encoding): Reader = {
    new BufferedReader(
      new InputStreamReader(getInputStream, enc))
  }

  def getReader(charset: Charset): Reader =
    getReader(charset.displayName())

  def getInputStream: InputStream

  def getInputAsString(enc: String = encoding): String =
    StringReaderUtil.inputStreamToString(getInputStream, enc)

  def getInputAsFutureString(enc: String = encoding)
                            (implicit ec: ExecutionContext): Future[String] =
    Future(getInputAsString(enc))

  def toLines(enc: String = encoding): Iterator[String] = {
    Source
      .fromInputStream(getInputStream, enc)
      .getLines
  }
}

object InputWrapper {
  private class InputStreamInputWrapper(val is: InputStream,
                                        val encoding: String)
    extends InputWrapper {
    override def getInputStream: InputStream = is
  }

  private class StringInputWrapper(val input: String,
                                   override val encoding: String)
    extends InputStreamInputWrapper(
      stringToInputStream(input, encoding),
      encoding
    )

  def apply(is: InputStream, encoding: String): InputWrapper =
    new InputStreamInputWrapper(is, encoding)

  def apply(input: String, encoding: String): InputWrapper =
    new StringInputWrapper(input, encoding)

  def stringToInputStream(input: String, encoding: String): InputStream = {
    new ReaderInputStream(new StringReader(input), encoding)
  }
}