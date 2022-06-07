package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun removeIndexOperation(array: Array<Any>, index: Int?): MutableList<Any> {
  val list = mutableListOf(*array)
  if (index == null) {
    list.removeLast()
  } else {
    list.removeAt(index)
  }
  return list
}

val removeIndex: OperationHandler = {
  val index = if (it.size >= 2) {
    (it.get(1) as Double).toInt()
  } else {
    null
  }
  removeIndexOperation(it[0] as Array<Any>, index)
}
