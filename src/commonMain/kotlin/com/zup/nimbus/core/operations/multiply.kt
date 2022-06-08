package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.utils.times

private fun multiplyOperation(array: Array<Any?>): Number {
  var numbers = array
  if (array.size == 1 && array[0] is Array<*>) numbers = array[0] as Array<Any?>
  var acc: Number = 1
  numbers.forEach { acc *= (it as Number)  }
  return acc
}

val multiply: OperationHandler = { multiplyOperation(it) }
