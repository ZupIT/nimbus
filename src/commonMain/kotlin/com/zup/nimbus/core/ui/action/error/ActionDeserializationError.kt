package com.zup.nimbus.core.ui.action.error

import com.zup.nimbus.core.ActionEvent
import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import com.zup.nimbus.core.scope.getPathToScope

class ActionDeserializationError(event: ActionEvent, properties: AnyServerDrivenData): IllegalArgumentException() {
  override val message = "Error while deserializing properties for the action \"condition\".\nAt: " +
    "${event.scope.getPathToScope()}${properties.errorsAsString()}"
}
