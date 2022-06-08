package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

val eq: OperationHandler = { it[0] == it[1] }
