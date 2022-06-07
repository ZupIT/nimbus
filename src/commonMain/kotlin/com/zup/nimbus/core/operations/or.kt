package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

val or: OperationHandler = {
  var array = it
  if (it.size == 1 && it[0] is Array<*>) array = it[0] as Array<Any?>
  array.contains(true)
}
