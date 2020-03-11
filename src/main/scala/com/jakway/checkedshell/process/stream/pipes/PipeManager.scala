package com.jakway.checkedshell.process.stream.pipes

import java.io.{Closeable, InputStream, OutputStream}

import com.jakway.checkedshell.error.behavior.CloseBehavior
import com.jakway.checkedshell.error.behavior.CloseBehavior.CloseReturnType

trait PipeManager extends Closeable {
  /**
   * This exists so you can change the behavior of Closeable.close, which can't
   * take any parameters
   * @return
   */
  protected def getDefaultCloseBehavior: CloseBehavior

  def getInputStream: InputStream
  def getOutputStream: OutputStream

  def closeInputStream(implicit closeBehavior: CloseBehavior): CloseReturnType
  def closeOutputStream(implicit closeBehavior: CloseBehavior): CloseReturnType
  def closeAll(implicit closeBehavior: CloseBehavior): CloseReturnType

  override def close(): Unit = closeAll(getDefaultCloseBehavior)
}
