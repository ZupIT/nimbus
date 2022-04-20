package com.zup.nimbus.core.utils

fun <T, U>mapValuesToMutableList(list: List<T>, iteratee: (item: T) -> U): MutableList<U> {
  val result = ArrayList<U>()
  list.forEach { result.add(iteratee(it)) }
  return result
}
