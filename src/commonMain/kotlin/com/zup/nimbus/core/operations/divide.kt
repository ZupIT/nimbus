package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun divideOperation(array: Array<Any?>): Double {
  var numbers = array
  if (array.size == 1 && array[0] is Array<*>) numbers = array[0] as Array<Any?>
  var acc = (numbers[0] as Double) * (numbers[0] as Double)
  numbers.forEach { acc = (acc / it as Double)  }
  return acc
}

val divide: OperationHandler = { divideOperation(it) }
