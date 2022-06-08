package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

val concat: OperationHandler = {
  var array = it
  if (it.size == 1 && it[0] is Array<*>) array = it[0] as Array<Any?>
  array.reduce { accumulator, current -> "${accumulator}${current}" }
}
