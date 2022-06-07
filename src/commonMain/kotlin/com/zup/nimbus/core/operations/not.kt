package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

val not: OperationHandler = { !(it[0] as Boolean) }
