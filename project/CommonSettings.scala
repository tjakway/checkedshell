import sbt._
import Keys._

package checkedshell.project {
  object CommonSettings {
    object Names {
      val programName = "checkedshell"
      val mainProjectName = programName
      val programsProjectName = programName + "_util_programs"
    }
    val version = "0.1"
    val scalaVersion = "2.12.10"
    val scalacOptions = Seq("-unchecked", "-deprecation", "-feature")

    def apply(p: Project): Project = {
      p.settings(
        //make ScalaCheck give stack traces
        //see https://stackoverflow.com/questions/24396407/how-to-display-entire-stack-trace-for-thrown-exceptions-from-scalacheck-tests
        testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-verbosity", "2"))
       .settings(
        //scalatest recommends unbuffered test output 
        //see http://www.scalatest.org/user_guide/using_scalatest_with_sbt
        logBuffered in Test := false)
       
       //run tests sequentially
       .settings(parallelExecution in Test := false)
    }
  }
}
