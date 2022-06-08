package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun unionOperation(args: Array<Any?>): MutableList<Any> {
  val result: MutableList<Any> = ArrayList()
  args.forEach { list -> result.addAll(list as Array<Any>) }
  return result
}

val union: OperationHandler = { unionOperation(it) }
