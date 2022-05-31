package com.zup.nimbus.core.operations

fun sum(vararg args: Double): Double {
  return args.fold(0.0) { acc, number -> (acc + number) }
}
