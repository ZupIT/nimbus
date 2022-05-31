package com.zup.nimbus.core.operations

fun divide(vararg args: Double): Double {
  return args.fold(args[0] * args[0]) { acc, number -> (acc / number) }
}
