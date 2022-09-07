package com.zup.nimbus.core.action

import com.zup.nimbus.core.regex.toFastRegex
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.tree.ServerDrivenAction
import com.zup.nimbus.core.tree.stateful.ServerDrivenEvent
import com.zup.nimbus.core.tree.stateful.ServerDrivenNode
import com.zup.nimbus.core.tree.stateful.Stateful
import com.zup.nimbus.core.tree.stateful.find
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOfKey

private val statePathRegex = """^(\w+)((?:\.\w+)*)${'$'}""".toFastRegex()

private fun getPathToOrigin(origin: Stateful): String {
  return when (origin) {
    is ServerDrivenNode -> origin.id
    is ServerDrivenEvent -> "${getPathToOrigin(origin.parent)} > ${origin.name}"
    else -> ""
  }
}

internal fun setState(event: ActionTriggeredEvent) {
  val properties = event.action.properties
  val logger = event.origin.view.nimbusInstance.logger
  try {
    val path: String = valueOfKey(properties, "path")
    val value: Any? = valueOfKey(properties, "value")
    val matchResult = statePathRegex.findWithGroups(path) ?:
      return logger.error("""The path "$path" is not a valid state path.""")
    val (stateId, statePath) = matchResult.destructured
    val state = event.origin.find(stateId)
    if (state == null) {
      val message = """Could not find state "$stateId" from "${getPathToOrigin(event.origin)}""""
      logger.error(message)
    } else {
      state.set(value, statePath, false)
      event.dependencies.add(state)
    }
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while setting state.\n${e.message}")
  }
}
