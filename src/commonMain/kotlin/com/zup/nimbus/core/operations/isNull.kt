package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

val isNull: OperationHandler = { it[0] == null }
