package com.zup.nimbus.core.action

import com.zup.nimbus.core.render.ActionEvent
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOf

fun condition(event: ActionEvent) {
  val logger = event.view.nimbusInstance.logger
  val properties = event.action.properties
  try {
    val condition: Boolean = valueOf(properties, "condition")
    val onTrue: ((_: Any?) -> Unit)? = valueOf(properties, "onTrue")
    val onFalse: ((_: Any?) -> Unit)? = valueOf(properties, "onFalse")
    if (condition && onTrue != null) onTrue(null)
    else if (!condition && onFalse != null) onFalse(null)
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while executing conditional action.\n${e.message}")
  }
}
