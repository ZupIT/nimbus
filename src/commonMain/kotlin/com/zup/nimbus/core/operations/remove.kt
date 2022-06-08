package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun removeOperation(array: Any?, item: Any): MutableList<Any?> {
  if (array is Array<*>) {
    val list = mutableListOf(*array)
    list.remove(item)
    return list
  }
  if (array is List<*>) {
    val list = array.toMutableList()
    list.remove(item)
    return list
  }
  return mutableListOf()
}

val remove: OperationHandler = { removeOperation(it[0], it[1] as Any) }
