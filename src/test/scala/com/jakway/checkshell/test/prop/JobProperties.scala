package com.jakway.checkshell.test.prop

import com.jakway.checkedshell.common.Echo
import com.jakway.checkedshell.process.TaskJob
import com.jakway.checkedshell.test.framework.HasDefaultTestConfig
import org.scalacheck.Gen
import org.scalatest.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.propspec.AnyPropSpec

import scala.concurrent.Await

class JobProperties
  extends AnyPropSpec
    with HasDefaultTestConfig
    with ScalaCheckPropertyChecks
    with Matchers {

  val genStr = Gen.alphaNumStr

  property("flatMap pipes properly") {
    forAll(Gen.alphaNumStr) { (str: String) =>
      val future = TaskJob(new Echo(false, Seq(str)))
        .run(None)

      Await.result(future, getTestConfig.futureTimeOut).stdout shouldEqual str
    }
  }
}
