package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

internal fun getDefaultOperations(): Map<String, OperationHandler> {
  return getArrayOperations() + getOtherOperations() + getNumberOperations() + getStringOperations() +
    getLogicOperations()
}
