package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun removeIndexOperation(array: Any?, index: Int?): MutableList<Any?> {
  var list = mutableListOf<Any?>()
  if (array is Array<*>) {
    list = array.toMutableList()
    if (index == null) list.removeLast() else list.removeAt(index)
  }
  if (array is List<*>) {
    list = array.toMutableList()
    if (index == null) list.removeLast() else list.removeAt(index)
  }
  return list
}

val removeIndex: OperationHandler = {
  val index = if (it.size >= 2) (it.get(1) as Number).toInt() else null
  removeIndexOperation(it[0], index)
}
