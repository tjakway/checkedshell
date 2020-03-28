package com.jakway.checkedshell.process.stream.pipes

import java.nio.channels.Pipe

import com.jakway.checkedshell.error.behavior.CloseBehavior

trait WithPipeManagerConstructors {
  def apply(implicit closeBehavior: CloseBehavior): PipeManager = apply(None)

  def apply(description: String)
           (implicit closeBehavior: CloseBehavior): PipeManager =
    apply(Some(description))

  def apply(optDescription: Option[String])
           (implicit closeBehavior: CloseBehavior): PipeManager =
    new ChannelPipeManager(Pipe.open(), optDescription, closeBehavior)
}
