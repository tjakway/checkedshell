package com.jakway.checkedshell.error.cause

trait ErrorCause {
  val description: String = toString
}

object ErrorCause {
  /**
   * return or wrap the errors
   * @param xs
   * @return
   */
  def apply(xs: Set[ErrorCause]): ErrorCause = {
    if(xs.size == 1) {
      xs.head
    } else {
      MultipleErrors(xs)
    }
  }
}








