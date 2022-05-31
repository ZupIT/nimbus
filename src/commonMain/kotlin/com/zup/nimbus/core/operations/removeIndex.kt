package com.zup.nimbus.core.operations

fun removeIndex(list: MutableList<Any>, index: Int?): MutableList<Any> {
  if (index == null) {
    list.removeLast()
  } else {
    list.removeAt(index)
  }
  return list
}
