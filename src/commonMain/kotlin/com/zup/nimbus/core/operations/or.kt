package com.zup.nimbus.core.operations

fun or(vararg args: Boolean): Boolean {
  return args.contains(true)
}
