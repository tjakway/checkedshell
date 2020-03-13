package com.jakway.checkedshell.config

import java.nio.charset.StandardCharsets

import scala.concurrent.ExecutionContext

class Config {

}

object Config {
  val defaultEncoding: String = StandardCharsets.UTF_8.displayName()

  object DefaultImplicits {
    implicit lazy val defaultEc: ExecutionContext = scala.concurrent.ExecutionContext.global
    implicit lazy val defaultRc: RunConfiguration = RunConfiguration.default
  }
}
