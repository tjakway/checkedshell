package com.jakway.checkedshell.process.stream.pipes

import java.io.{Closeable, InputStream, OutputStream}

import com.jakway.checkedshell.error.behavior.CloseBehavior
import com.jakway.checkedshell.error.behavior.CloseBehavior.CloseReturnType
import com.jakway.checkedshell.process.stream.pipes.input.InputWrapper
import com.jakway.checkedshell.process.stream.pipes.output.OutputWrapper

trait PipeManager extends Closeable {
  /**
   * This exists so you can change the behavior of Closeable.close, which can't
   * take any parameters
   * @return
   */
  protected def getDefaultCloseBehavior: CloseBehavior =
    CloseBehavior.default()

  def getInputStream: InputStream
  def getOutputStream: OutputStream

  def getOutputWrapper(enc: String): OutputWrapper
  def getInputWrapper(enc: String): InputWrapper

  def closeInputStream(implicit closeBehavior: CloseBehavior): CloseReturnType
  def closeOutputStream(implicit closeBehavior: CloseBehavior): CloseReturnType
  def closeAll(implicit closeBehavior: CloseBehavior): CloseReturnType

  override def close(): Unit = closeAll(getDefaultCloseBehavior)
}

object PipeManager extends WithPipeManagerConstructors {
  def newWrapperPair(enc: String,
                     optDescription: Option[String])
                    (implicit closeBehavior: CloseBehavior):
    (InputWrapper, OutputWrapper) = {

    val pm = apply(optDescription)
    (pm.getInputWrapper(enc), pm.getOutputWrapper(enc))
  }
}