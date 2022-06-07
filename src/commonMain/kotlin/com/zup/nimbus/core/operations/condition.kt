package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.utils.then

private fun conditionFunction(premise: Boolean, trueValue: Any, falseValue: Any): Any {
  return ((premise) then trueValue) ?: falseValue
}

val condition: OperationHandler = { conditionFunction(it[0] as Boolean, it[1] as Any, it[2] as Any) }
