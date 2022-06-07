package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun multiplyOperation(array: Array<Any?>): Double {
  var numbers = array
  if (array.size == 1 && array[0] is Array<*>) numbers = array[0] as Array<Any?>
  var acc = 1.0
  numbers.forEach { acc = (acc * it as Double)  }
  return acc
}

val multiply: OperationHandler = { multiplyOperation(it) }
