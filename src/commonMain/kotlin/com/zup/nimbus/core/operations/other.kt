package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

internal fun getOtherOperations(): Map<String, OperationHandler> {
  return mapOf(
    "contains" to {
      val (collection, element) = it
      when (collection) {
        is List<*> -> collection.contains(element)
        is Map<*, *> -> collection.contains(element)
        is String -> collection.contains(element as String)
        else -> false
      }
    },
    "concat" to {
      when (it[0]) {
        is List<*> -> {
          val result = ArrayList<Any?>()
          it.forEach { list ->
            if (list is List<*>) result.addAll(list)
          }
          result
        }
        is Map<*, *> -> {
          val result = HashMap<Any?, Any?>()
          it.forEach { map ->
            if (map is Map<*, *>) result.putAll(map)
          }
          result
        }
        else -> it.reduce { result, item -> "${result}${item}" }
      }
    },
    "length" to {
      when (val collection = it[0]) {
        is List<*> -> collection.size
        is Map<*, *> -> collection.size
        is String -> collection.length
        else -> 0
      }
    },
    "eq" to {
      val (left, right) = it
      if (left is Number && right is Number) left.toDouble() == right.toDouble()
      else left == right
    },
    "isNull" to {
      it[0] == null
    },
    "isEmpty" to {
      when (val collection = it[0]) {
        is List<*> -> collection.isEmpty()
        is Map<*, *> -> collection.isEmpty()
        is String -> collection.isEmpty()
        else -> collection == null
      }
    }
  )
}
