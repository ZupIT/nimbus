package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun removeOperation(array: Any?, item: Any): MutableList<Any?> {
  var list = mutableListOf<Any?>()
  if (array is Array<*>) {
    list = array.toMutableList()
    list.remove(item)
  }
  else if (array is List<*>) {
    list = array.toMutableList()
    list.remove(item)
  }
  return list
}

val remove: OperationHandler = { removeOperation(it[0], it[1] as Any) }
