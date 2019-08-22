package com.jakway.checkshell.test.prop

import com.jakway.checkedshell.common.Echo
import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.TaskJob
import com.jakway.checkedshell.test.framework.HasDefaultTestConfig
import com.jakway.checkshell.test.framework.WithJobOutputMatcher
import org.scalacheck.Gen
import org.scalatest.Matchers
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class EchoProperties
  extends AnyPropSpec
    with HasDefaultTestConfig
    with WithJobOutputMatcher
    with ScalaCheckPropertyChecks
    with Matchers {

  val genStr = Gen.alphaNumStr

  property("flatMap pipes properly") {
    forAll(Gen.alphaNumStr) { (str: String) =>
      val future = TaskJob(new Echo(false, Seq(str)))
        .run(None)

      val expectedOutput = new ProgramOutput(0, str, "")
      future should matchJobOutput(expectedOutput)
      //Await.result(future, getTestConfig.futureTimeOut).stdout shouldEqual str
    }
  }
}
