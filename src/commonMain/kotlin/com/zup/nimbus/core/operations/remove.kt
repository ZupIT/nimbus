package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun removeOperation(array: Array<Any>, item: Any): MutableList<Any> {
  val list = mutableListOf(*array)
  list.remove(item)
  return list
}

val remove: OperationHandler = { removeOperation(it[0] as Array<Any>, it[1] as Any) }
