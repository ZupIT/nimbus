package com.zup.nimbus.core.action

import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOfKey

internal fun condition(event: ActionTriggeredEvent) {
  val logger = event.origin.view.nimbusInstance.logger
  val properties = event.action.properties
  try {
    val condition: Boolean = valueOfKey(properties, "condition")
    val onTrue: ((_: Any?) -> Unit)? = valueOfKey(properties, "onTrue")
    val onFalse: ((_: Any?) -> Unit)? = valueOfKey(properties, "onFalse")
    if (condition && onTrue != null) onTrue(null)
    else if (!condition && onFalse != null) onFalse(null)
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while executing conditional action.\n${e.message}")
  }
}
