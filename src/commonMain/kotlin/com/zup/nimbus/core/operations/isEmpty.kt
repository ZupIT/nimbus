package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun isEmptyOperation(value: Any?): Boolean {
  if (value == null) {
    return true
  }
  else if (value is Array<*>) {
    return value.isEmpty()
  }
  if (value is List<*>) {
    return value.isEmpty()
  }
  if (value is Map<*, *>) {
    return value.isEmpty()
  }
  if (value is String) {
    return value.isEmpty()
  }
  return false
}

val isEmpty: OperationHandler = { isEmptyOperation(it[0]) }
