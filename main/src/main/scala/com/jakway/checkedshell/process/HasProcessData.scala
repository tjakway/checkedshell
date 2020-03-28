package com.jakway.checkedshell.process

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.data.ProcessData
import com.jakway.checkedshell.error.ErrorData
import com.jakway.checkedshell.error.cause.{CloseStreamError, CloseStreamErrors}

import scala.util.{Failure, Success, Try}

trait HasProcessData[A] {
  def copyWithProcessData(newProcessData: ProcessData): A

}
