package br.com.zup.nimbus.core.ui.action.error

import br.com.zup.nimbus.core.ActionEvent
import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.scope.getPathToScope

class ActionDeserializationError(event: ActionEvent, properties: AnyServerDrivenData): IllegalArgumentException() {
  override val message = "Error while deserializing properties for the action \"${event.action.name}\".\nAt: " +
    "${event.scope.getPathToScope()}${properties.errorsAsString()}"
}
