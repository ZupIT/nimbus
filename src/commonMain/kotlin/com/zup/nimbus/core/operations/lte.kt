package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

val lte: OperationHandler = { (it[0] as Double) <= (it[1] as Double) }
