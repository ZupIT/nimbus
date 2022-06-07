package com.zup.nimbus.core.action

import com.zup.nimbus.core.render.ActionEvent
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOf

fun setState(event: ActionEvent) {
  val nimbus = event.view.nimbusInstance
  val view = event.view
  val node = event.node
  val properties = event.action.properties
  try {
    val path: String = valueOf(properties, "path")
    val value = valueOf<Any>(properties, "value")
    view.renderer.setState(node, path, value)
  } catch (e: UnexpectedDataTypeError) {
    nimbus.logger.error("Error while attempting to set state .\n${e.message}")
  }
}
