package br.com.zup.nimbus.core.ui.action

import br.com.zup.nimbus.core.log.LogLevel
import br.com.zup.nimbus.core.ActionTriggeredEvent
import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.ui.action.error.ActionDeserializationError

internal fun log(event: ActionTriggeredEvent) {
  val logger = event.scope.nimbus.logger
  val properties = AnyServerDrivenData(event.action.properties)
  val message = properties.get("message").asString()
  val level = properties.get("level").asEnumOrNull(LogLevel.values()) ?: LogLevel.Info
  if (properties.hasError()) throw ActionDeserializationError(event, properties)
  logger.log(message, level)
}
