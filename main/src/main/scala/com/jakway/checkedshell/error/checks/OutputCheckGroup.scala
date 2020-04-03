package com.jakway.checkedshell.error.checks

case class OutputCheckGroup(taggedChecks: Seq[TaggedOutputChecks],
                            optDescription: Option[String] = None)
  extends ErrorCheckGroup[OutputCheck] {

  override def allChecks: Seq[OutputCheck] = {
    taggedChecks.foldLeft(Seq.empty: Seq[OutputCheck]) {
      case (acc, TaggedOutputChecks(xs, _)) => acc ++ xs
    }
  }
}
