package com.jakway.checkedshell.data

class JobOutputStream

class NumberedJobOutputStream(val num: Int)
  extends JobOutputStream

class StandardJobOutputStream(override val num: Int)
  extends NumberedJobOutputStream(num)

object StandardJobOutputStream {
  object Stdout extends StandardJobOutputStream(1)
  object Stderr extends StandardJobOutputStream(2)
}
