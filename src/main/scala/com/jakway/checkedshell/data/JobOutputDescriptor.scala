package com.jakway.checkedshell.data

class JobOutputDescriptor

class NumberedJobOutputDescriptor(val num: Int)
  extends JobOutputDescriptor {

  override def equals(obj: Any): Boolean = obj match {
    case other: NumberedJobOutputDescriptor =>
      other.num == num
    case _ => false
  }
}

class StandardJobOutputDescriptor(override val num: Int)
  extends NumberedJobOutputDescriptor(num)

object StandardJobOutputDescriptor {
  object Stdout extends StandardJobOutputDescriptor(1)
  object Stderr extends StandardJobOutputDescriptor(2)
}
