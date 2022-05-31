package com.zup.nimbus.core.operations

fun remove(list: MutableList<Any>, item: Any): MutableList<Any> {
  list.remove(item)
  return list
}
