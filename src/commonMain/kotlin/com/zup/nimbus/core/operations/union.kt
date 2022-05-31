package com.zup.nimbus.core.operations

fun <T> union(vararg args: MutableList<T>): MutableList<T> {
  val result: MutableList<T> = ArrayList()
  args.forEach { list: List<T> -> result.addAll(list) }
  return result
}
