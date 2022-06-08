package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun matchOperation(text: String, matcher: String): Boolean {
  return text.matches(matcher.toRegex())
}

val match: OperationHandler = { matchOperation(it[0] as String, it[1] as String) }
