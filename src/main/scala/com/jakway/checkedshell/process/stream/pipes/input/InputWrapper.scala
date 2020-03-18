package com.jakway.checkedshell.process.stream.pipes.input

import java.io._
import java.nio.charset.Charset

import com.jakway.checkedshell.process.stream.pipes.StreamWrapper
import com.jakway.checkedshell.util.StringReaderUtil
import org.apache.commons.io.input.ReaderInputStream

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

trait InputWrapper extends StreamWrapper {
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
  private class InputStreamInputWrapper(
    val is: InputStream,
    val encoding: String,
    override val optDescription: Option[String])
    extends InputWrapper {
    override def getInputStream: InputStream = is
  }

  private class StringInputWrapper(val input: String,
                                   override val encoding: String,
                                   override val optDescription: Option[String])
    extends InputStreamInputWrapper(
      stringToInputStream(input, encoding),
      encoding,
      optDescription
    )

  def apply(is: InputStream, encoding: String, desc: Option[String]): InputWrapper =
    new InputStreamInputWrapper(is, encoding, desc)

  def apply(input: String,
            encoding: String, desc: Option[String]): InputWrapper =
    new StringInputWrapper(input, encoding, desc)

  def stringToInputStream(input: String, encoding: String): InputStream = {
    new ReaderInputStream(new StringReader(input), encoding)
  }
}