package com.zup.nimbus.core.operations

fun concat(vararg args: String): String {
  return args.reduce { accumulator, current -> "${accumulator}${current}" }
}
