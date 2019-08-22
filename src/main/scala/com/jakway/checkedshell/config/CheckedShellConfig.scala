package com.jakway.checkedshell.config

import scala.concurrent.ExecutionContext

class CheckedShellConfig {

}

object CheckedShellConfig {
  object DefaultImplicits {
    implicit lazy val defaultEc: ExecutionContext = scala.concurrent.ExecutionContext.global
    implicit lazy val defaultRc: RunConfiguration = RunConfiguration.default
  }
}
