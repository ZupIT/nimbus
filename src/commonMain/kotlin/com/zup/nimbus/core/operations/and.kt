package com.zup.nimbus.core.operations

fun and(vararg args: Boolean): Boolean {
  return !args.contains(false)
}
