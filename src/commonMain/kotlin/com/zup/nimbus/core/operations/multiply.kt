package com.zup.nimbus.core.operations

fun multiply(vararg args: Double): Double {
  return args.fold(1.0) { acc, number -> (acc * number) }
}
