package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

val lowercase: OperationHandler = { (it[0] as String).lowercase() }
