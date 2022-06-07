package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

val uppercase: OperationHandler = { (it[0] as String).uppercase() }
