package com.jakway.checkedshell.test.framework

import com.jakway.checkedshell.config.RunConfiguration

import scala.concurrent.ExecutionContext

case class TestConfig(rc: RunConfiguration,
                      ec: ExecutionContext)

object TestConfig {
  val default: TestConfig = {
    val rc = RunConfiguration.default
    val ec = scala.concurrent.ExecutionContext.global

    TestConfig(rc, ec)
  }
}

trait HasTestConfig {
  def getTestConfig: TestConfig

  implicit lazy val rc: RunConfiguration = getTestConfig.rc
  implicit lazy val ec: ExecutionContext = getTestConfig.ec
}

trait HasDefaultTestConfig
  extends HasTestConfig {

  override def getTestConfig: TestConfig = TestConfig.default
}