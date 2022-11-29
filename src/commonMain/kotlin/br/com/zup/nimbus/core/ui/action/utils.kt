package br.com.zup.nimbus.core.ui.action

import br.com.zup.nimbus.core.ActionEvent
import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.deserialization.SerializationError
import br.com.zup.nimbus.core.ui.action.error.ActionExecutionError

/**
 * Utility for attempting a json serialization from within an action handler.
 */
internal fun attemptJsonSerialization(value: AnyServerDrivenData, event: ActionEvent) =
  if (value.isNull()) null
  else try {
    value.toJson()
  } catch (e: SerializationError) {
    throw ActionExecutionError(event, e)
  }
