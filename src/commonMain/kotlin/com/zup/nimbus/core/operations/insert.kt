package com.zup.nimbus.core.operations

fun <T> insert(list: MutableList<T>, item: T, index: Int? = null): MutableList<T> {
  if (index != null) {
    list.add(index, item)
  }
  else {
    list.add(item)
  }
  return list
}
