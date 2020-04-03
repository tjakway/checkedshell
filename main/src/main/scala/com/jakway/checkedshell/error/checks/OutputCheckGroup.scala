package com.jakway.checkedshell.error.checks

import java.util.Formatter

case class OutputCheckGroup(taggedChecks: Seq[TaggedOutputChecks])
  extends ErrorCheckGroup[OutputCheck] {

  override def allChecks: Seq[OutputCheck] = {
    taggedChecks.foldLeft(Seq.empty: Seq[OutputCheck]) {
      case (acc, TaggedOutputChecks(xs, _, _)) => acc ++ xs
    }
  }

  private def formatDescription: String = {
    val fmt = new Formatter(new StringBuffer())

    fmt.format("{\n")
    taggedChecks.foreach { thisCheck =>
      fmt.format("\t%s", thisCheck.toString)
    }
    fmt.format("\n}")

    fmt.toString.trim
  }

  override lazy val optDescription: Option[String] = {
    if(taggedChecks.isEmpty) {
      None
    } else {
      Some(formatDescription)
    }
  }

  override def toString: String = {
    val fmt = new Formatter(new StringBuffer())

    fmt.format("%s(%s, %s)",
      getClass.getName,
      taggedChecks.toString(),
      optDescription.getOrElse("< no description >"))

    fmt.toString.trim
  }
}
