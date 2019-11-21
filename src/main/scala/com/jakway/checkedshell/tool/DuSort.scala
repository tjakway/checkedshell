package com.jakway.checkedshell.tool

import com.jakway.checkedshell.config.RunConfiguration
import com.jakway.checkedshell.process.Process

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, duration}

object DuSort {
  def main(args: Array[String]): Unit = {
    implicit val rc = RunConfiguration.default
    implicit val ec = scala.concurrent.ExecutionContext.global

    val res = Await.result(Process("du", Seq("-h", "-d", "1"))
      .flatMap(Process("sort", Seq("-h"))).run(None), Duration(10, duration.SECONDS))
    println(res.stdout)
  }

}
