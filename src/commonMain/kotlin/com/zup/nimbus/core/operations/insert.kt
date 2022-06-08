package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun insertOperation(array: Array<Any>, item: Any, index: Int?): MutableList<Any> {
  val list = array.toMutableList()
  if (index != null) list.add(index, item) else list.add(item)
  return list
}

val insert: OperationHandler = {
  val index = if (it.size >= 3) (it[2] as Number).toInt() else null
  insertOperation(it[0] as Array<Any>, it[1] as Any, index)
}
