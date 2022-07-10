package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.utils.then

private fun toBooleanList(values: Array<Any?>): List<Boolean> {
  return values.toList().filterIsInstance<Boolean>()
}

internal fun getLogicOperations(): Map<String, OperationHandler> {
  return mapOf(
    "and" to {
      !toBooleanList(it).contains(false)
    },
    "or" to {
      toBooleanList(it).contains(true)
    },
    "not" to {
      !(it[0] as Boolean)
    },
    "condition" to {
      val premise = it[0] as Boolean
      val trueValue = it[1]
      val falseValue = it[2]
      ((premise) then trueValue) ?: falseValue
    }
  )
}
