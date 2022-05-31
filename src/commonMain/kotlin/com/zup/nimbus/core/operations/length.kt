package com.zup.nimbus.core.operations

fun length(value: Any): Int {
  if (value is Array<*>) {
    return value.size
  }
  if (value is List<*>) {
    return value.size
  }
  if (value is Map<*, *>) {
    return value.size
  }
  if (value is String) {
    return value.length
  }
  return value.toString().length
}
