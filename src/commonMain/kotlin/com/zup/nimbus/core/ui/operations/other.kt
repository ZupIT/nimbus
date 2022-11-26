package com.zup.nimbus.core.ui.operations

import com.zup.nimbus.core.ui.UILibrary
import com.zup.nimbus.core.utils.compareTo
import com.zup.nimbus.core.utils.toNumberOrNull

private fun compareNumbers(left: Any?, right: Any?): Boolean {
  val leftNumber = toNumberOrNull(left) ?: return false
  val rightNumber = toNumberOrNull(right) ?: return false
  return leftNumber.compareTo(rightNumber) == 0
}

@Suppress("ComplexMethod")
internal fun registerOtherOperations(library: UILibrary) {
  library
    .addOperation("contains"){
      val (collection, element) = it
      when (collection) {
        is List<*> -> collection.contains(element)
        is Map<*, *> -> collection.contains(element)
        is String -> collection.contains(element as String)
        else -> false
      }
    }
    .addOperation("concat"){
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
    }
    .addOperation("length"){
      when (val collection = it[0]) {
        is List<*> -> collection.size
        is Map<*, *> -> collection.size
        is String -> collection.length
        else -> 0
      }
    }
    .addOperation("eq"){
      val (left, right) = it
      if (left == right) true
      else compareNumbers(left, right)
    }
    .addOperation("isNull"){
      it[0] == null
    }
    .addOperation("isEmpty"){
      when (val collection = it[0]) {
        is List<*> -> collection.isEmpty()
        is Map<*, *> -> collection.isEmpty()
        is String -> collection.isEmpty()
        else -> collection == null
      }
    }
}
