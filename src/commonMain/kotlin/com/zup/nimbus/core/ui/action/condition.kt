package com.zup.nimbus.core.ui.action

import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import com.zup.nimbus.core.ui.action.error.ActionDeserializationError

internal fun condition(event: ActionTriggeredEvent) {
  val properties = AnyServerDrivenData(event.action.properties)
  val condition = properties.get("condition").asBoolean()
  val onTrue = properties.get("onTrue").asEventOrNull()
  val onFalse = properties.get("onFalse").asEventOrNull()
  if (properties.hasError()) throw ActionDeserializationError(event, properties)
  if (condition) onTrue?.run()
  else onFalse?.run()
}
