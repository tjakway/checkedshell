package com.jakway.checkedshell.test.framework

import com.jakway.checkedshell.config.RunConfiguration

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

case class TestConfig(futureTimeOut: Duration,
                      rc: RunConfiguration,
                      ec: ExecutionContext)

object TestConfig {
  val defaultFutureTimeOut: Duration =
    Duration(5, scala.concurrent.duration.SECONDS)

  val default: TestConfig = {
    val rc = RunConfiguration.default
    val ec = scala.concurrent.ExecutionContext.global

    TestConfig(defaultFutureTimeOut,
      rc, ec)
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