package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.utils.minus
import com.zup.nimbus.core.utils.times

private fun subtractOperation(array: Array<Any?>): Number {
  var numbers = array
  if (array.size == 1 && array[0] is Array<*>) numbers = array[0] as Array<Any?>
  var acc: Number = (numbers[0] as Number) * 2
  numbers.forEach { acc -= (it as Number)  }
  return acc
}

val subtract: OperationHandler = { subtractOperation(it) }
