package br.com.zup.nimbus.core.ui.action.error

import br.com.zup.nimbus.core.ActionEvent
import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.scope.getPathToScope
import br.com.zup.nimbus.core.utils.addPrefix

class ActionDeserializationError(event: ActionEvent, message: String): IllegalArgumentException() {
  constructor(event: ActionEvent, properties: AnyServerDrivenData): this(event, properties.errorsAsString())
  override val message = "Error while deserializing properties for the action \"${event.action.name}\".\nAt: " +
    "${event.scope.getPathToScope()}${addPrefix(message, "\n")}"
}
