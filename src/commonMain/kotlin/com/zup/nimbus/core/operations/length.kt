package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun lengthOperation(value: Any): Int {
  if (value is Array<*>) {
    return value.size
  }
  if (value is List<*>) {
    return value.size
  }
  if (value is Map<*,*>) {
    return value.size
  }
  if (value is String) {
    return value.length
  }
  return value.toString().length
}

val length: OperationHandler = { lengthOperation(it[0] as Any) }
