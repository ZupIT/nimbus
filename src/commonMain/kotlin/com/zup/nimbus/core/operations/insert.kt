package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private const val MIN_ARRAY_SIZE = 3
private const val INDEX_POSITION = 2

private fun insertOperation(array: Array<Any>, item: Any, index: Int?): MutableList<Any> {
  val list = array.toMutableList()
  if (index != null) list.add(index, item) else list.add(item)
  return list
}

val insert: OperationHandler = {
  val index = if (it.size >= MIN_ARRAY_SIZE) (it[INDEX_POSITION] as Number).toInt() else null
  insertOperation(it[0] as Array<Any>, it[1] as Any, index)
}
