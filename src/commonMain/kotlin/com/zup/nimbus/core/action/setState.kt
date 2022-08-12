package com.zup.nimbus.core.action

import com.zup.nimbus.core.render.ActionEvent
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOfKey

internal fun setState(event: ActionEvent) {
  val properties = event.action.properties
  try {
    val path: String = valueOfKey(properties, "path")
    val value: Any? = valueOfKey(properties, "value")
    event.view.renderer.setState(event.node, path, value)
  } catch (e: UnexpectedDataTypeError) {
    event.view.nimbusInstance.logger.error("Error while setting state.\n${e.message}")
  }
}
