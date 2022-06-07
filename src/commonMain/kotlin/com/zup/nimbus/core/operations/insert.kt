package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun insertOperation(array: Array<Any>, item: Any, index: Int? = null): MutableList<Any> {
  val list = mutableListOf(*array)
  if (index != null) {
    list.add(index, item)
  }
  else {
    list.add(item)
  }
  return list
}

val insert: OperationHandler = {
  val index = if (it.size >= 3) {
    (it.get(2) as Double).toInt()
  } else {
    null
  }
  insertOperation(it[0] as Array<Any>, it[1] as Any, index)
}
