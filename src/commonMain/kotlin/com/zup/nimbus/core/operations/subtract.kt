package com.zup.nimbus.core.operations

fun subtract(vararg args: Double): Double {
  return args.fold(args[0] * 2) { acc, number -> (acc - number) }
}
