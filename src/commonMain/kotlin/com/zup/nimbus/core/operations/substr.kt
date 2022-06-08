package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun substrOperation(text: String, startIndex: Int, endIndex: Int): String {
  return text.substring(startIndex, endIndex)
}

val substr: OperationHandler = { substrOperation(it[0] as String, it[1] as Int, it[2] as Int) }
