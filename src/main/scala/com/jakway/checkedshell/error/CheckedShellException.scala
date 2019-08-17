package com.jakway.checkedshell.error

class CheckedShellException(val msg: String)
  extends RuntimeException(msg)
