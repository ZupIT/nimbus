package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.utils.div
import com.zup.nimbus.core.utils.times

private fun divideOperation(array: Array<Any?>): Number {
  var numbers = array
  if (array.size == 1 && array[0] is Array<*>) numbers = array[0] as Array<Any?>
  var acc: Number = (numbers[0] as Number) * (numbers[0] as Number)
  numbers.forEach { acc /= (it as Number)  }
  return acc
}

val divide: OperationHandler = { divideOperation(it) }
