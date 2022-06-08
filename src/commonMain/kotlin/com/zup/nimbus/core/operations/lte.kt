package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.utils.compareTo

val lte: OperationHandler = { (it[0] as Number) <= (it[1] as Number) }
