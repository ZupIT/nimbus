package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.utils.capitalizeFirstLetter

val capitalize: OperationHandler = { capitalizeFirstLetter(it[0] as String) }
