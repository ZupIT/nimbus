package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

val contains: OperationHandler =  {
  if (it[0] is List<*>) {
    (it[0] as List<*>).contains(it[1])
  }
  else if (it[0] is Array<*>) {
    (it[0] as Array<*>).contains(it[1])
  }
  else if (it[0] is String) {
    (it[0] as String).contains((it[1]) as String)
  }
  else {
    false
  }
}
