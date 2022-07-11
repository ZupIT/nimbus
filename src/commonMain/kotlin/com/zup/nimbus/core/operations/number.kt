package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.utils.div
import com.zup.nimbus.core.utils.minus
import com.zup.nimbus.core.utils.plus
import com.zup.nimbus.core.utils.times

private fun toNumberList(values: Array<Any?>): List<Number> {
  val result = ArrayList<Number>()
  values.forEach {
    if (it is Number) result.add(it)
    else if (it is String) result.add(it.toDouble())
  }
  return result
}

internal fun getNumberOperations(): Map<String, OperationHandler> {
  return mapOf(
    "sum" to {
      toNumberList(it).reduce { result, item -> result.plus(item) }
    },
    "subtract" to {
      toNumberList(it).reduce { result, item -> result.minus(item) }
    },
    "multiply" to {
      toNumberList(it).reduce { result, item -> result.times(item) }
    },
    "divide" to {
      toNumberList(it).reduce { result, item -> result.div(item) }
    },
    "gt" to {
      val (left, right) = toNumberList(it)
      left.toDouble() > right.toDouble()
    },
    "gte" to {
      val (left, right) = toNumberList(it)
      left.toDouble() >= right.toDouble()
    },
    "lt" to {
      val (left, right) = toNumberList(it)
      left.toDouble() < right.toDouble()
    },
    "lte" to {
      val (left, right) = toNumberList(it)
      left.toDouble() <= right.toDouble()
    },
  )
}
