package com.jakway.checkshell.test.prop

import com.jakway.checkedshell.common.Echo
import com.jakway.checkedshell.data.ProgramOutput
import com.jakway.checkedshell.process.Job.JobOutput
import com.jakway.checkedshell.process.TaskJob
import com.jakway.checkedshell.test.framework.HasDefaultTestConfig
import com.jakway.checkshell.test.framework.WithJobOutputMatcher
import org.scalacheck.Gen
import org.scalatest.Matchers
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.Await

class EchoProperties
  extends AnyPropSpec
    with HasDefaultTestConfig
    with WithJobOutputMatcher
    with ScalaCheckPropertyChecks
    with Matchers {

  private def echoTest(e: Echo): JobOutput = {
    TaskJob(e).run(None)
  }

  property("flatMap pipes properly") {
    forAll(Gen.alphaNumStr) { (str: String) =>
      val expectedOutput = new ProgramOutput(0, str, "")
      echoTest(new Echo(false, Seq(str))) should matchJobOutput(expectedOutput)
      //Await.result(future, getTestConfig.futureTimeOut).stdout shouldEqual str
    }
  }

  property("prints with line separator") {
    forAll(Gen.alphaNumStr) { (str: String) =>
      val futureTestRes = echoTest(new Echo(true, Seq(str)))
      val testRes: ProgramOutput = Await.result(futureTestRes, getTestConfig.futureTimeOut)

      testRes.stdout.endsWith(System.lineSeparator()) shouldBe true
    }
  }
}
