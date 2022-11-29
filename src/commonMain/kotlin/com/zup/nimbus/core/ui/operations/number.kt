package com.zup.nimbus.core.ui.operations

import com.zup.nimbus.core.ui.UILibrary
import com.zup.nimbus.core.utils.compareTo
import com.zup.nimbus.core.utils.div
import com.zup.nimbus.core.utils.minus
import com.zup.nimbus.core.utils.plus
import com.zup.nimbus.core.utils.times
import com.zup.nimbus.core.utils.toNumberOrNull

private fun toNumberList(values: List<Any?>) = values.map { toNumberOrNull(it) }

// examples: left == right; left > right; left <= right
private fun toLeftAndRight(values: List<Any?>): Pair<Number?, Number?> {
  val numberList = toNumberList(values)
  return numberList.firstOrNull() to numberList.getOrNull(1)
}

@Suppress("ComplexMethod")
internal fun registerNumberOperations(library: UILibrary) {
  library
    .addOperation("sum"){
      toNumberList(it).reduce { result, item -> if (result == null || item == null) null else result + item }
    }
    .addOperation("subtract"){
      toNumberList(it).reduce { result, item -> if (result == null || item == null) null else result - item }
    }
    .addOperation("multiply"){
      toNumberList(it).reduce { result, item -> if (result == null || item == null) null else result * item }
    }
    .addOperation("divide"){
      toNumberList(it).reduce { result, item -> if (result == null || item == null) null else result / item }
    }
    .addOperation("gt"){
      val (left, right) = toLeftAndRight(it)
      if (left == null || right == null) false
      else left > right
    }
    .addOperation("gte"){
      val (left, right) = toLeftAndRight(it)
      if (left == null || right == null) false
      else left >= right
    }
    .addOperation("lt"){
      val (left, right) = toLeftAndRight(it)
      if (left == null || right == null) false
      else left < right
    }
    .addOperation("lte"){
      val (left, right) = toLeftAndRight(it)
      if (left == null || right == null) false
      else left <= right
    }
}
