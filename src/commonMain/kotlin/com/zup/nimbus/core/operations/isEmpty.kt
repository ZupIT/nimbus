package com.zup.nimbus.core.operations

fun <T> isEmpty(value: T?): Boolean {
  if (value == null) {
    return true
  }
  if (value is Array<*>) {
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
