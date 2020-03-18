package com.jakway.checkedshell.util

import org.slf4j.Logger

trait LogFunctions {
  type LogF = Logger => String => Unit

  def trace: LogF = l => m => l.trace(m)
  def debug: LogF = l => m => l.debug(m)
  def info: LogF = l => m => l.info(m)
  def warn: LogF = l => m => l.warn(m)
  def error: LogF = l => m => l.error(m)

  def doNothing: LogF = l => m => {}
}

object LogFunctions extends LogFunctions
