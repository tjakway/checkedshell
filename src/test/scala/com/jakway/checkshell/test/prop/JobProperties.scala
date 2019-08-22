package com.jakway.checkshell.test.prop

import com.jakway.checkedshell.common.Echo
import com.jakway.checkedshell.process.TaskJob
import com.jakway.checkedshell.test.framework.HasDefaultTestConfig
import org.scalacheck.Gen
import org.scalatest.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.propspec.AnyPropSpec

class JobProperties
  extends AnyPropSpec
    with HasDefaultTestConfig
    with ScalaCheckPropertyChecks
    with Matchers {

  val genStr = Gen.alphaNumStr

  property("flatMap pipes properly") {
    forAll(Gen.alphaNumStr) { (str: String) =>
      TaskJob(new Echo(false, Seq(str)))
        .run(None)
    }
  }
}
