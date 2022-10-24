package com.zup.nimbus.core.ui.operations

import com.zup.nimbus.core.ui.UILibrary
import com.zup.nimbus.core.utils.div
import com.zup.nimbus.core.utils.minus
import com.zup.nimbus.core.utils.plus
import com.zup.nimbus.core.utils.times

private fun toNumberList(values: List<Any?>): List<Number> {
  val result = ArrayList<Number>()
  values.forEach {
    if (it is Number) result.add(it)
    else if (it is String) result.add(it.toDouble())
  }
  return result
}

internal fun registerNumberOperations(library: UILibrary) {
  library
    .addOperation("sum"){
      toNumberList(it).reduce { result, item -> result.plus(item) }
    }
    .addOperation("subtract"){
      toNumberList(it).reduce { result, item -> result.minus(item) }
    }
    .addOperation("multiply"){
      toNumberList(it).reduce { result, item -> result.times(item) }
    }
    .addOperation("divide"){
      toNumberList(it).reduce { result, item -> result.div(item) }
    }
    .addOperation("gt"){
      val (left, right) = toNumberList(it)
      left.toDouble() > right.toDouble()
    }
    .addOperation("gte"){
      val (left, right) = toNumberList(it)
      left.toDouble() >= right.toDouble()
    }
    .addOperation("lt"){
      val (left, right) = toNumberList(it)
      left.toDouble() < right.toDouble()
    }
    .addOperation("lte"){
      val (left, right) = toNumberList(it)
      left.toDouble() <= right.toDouble()
    }
}
