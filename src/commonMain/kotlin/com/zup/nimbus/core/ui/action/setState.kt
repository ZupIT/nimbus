package com.zup.nimbus.core.ui.action

import com.zup.nimbus.core.regex.toFastRegex
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.tree.node.ServerDrivenNode
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOfKey

private val statePathRegex = """^(\w+)((?:\.\w+)*)${'$'}""".toFastRegex()

private fun getPathToScope(scope: Scope?): String {
  return when (scope) {
    is ServerDrivenNode -> scope.id
    is ServerDrivenEvent -> "${getPathToScope(scope.parent)} > ${scope.name}"
    else -> ""
  }
}

internal fun setState(event: ActionTriggeredEvent) {
  val properties = event.action.properties
  val sourceEvent = event.scope
  val logger: Logger by lazy { sourceEvent.nimbus.logger }

  try {
    val path: String = valueOfKey(properties, "path")
    val value: Any? = valueOfKey(properties, "value")
    val matchResult = statePathRegex.findWithGroups(path) ?:
      return logger.error("""The path "$path" is not a valid state path.""")
    val (stateId, statePath) = matchResult.destructured
    val state = sourceEvent.closestState(stateId)
    if (state == null) {
      val message = """Could not find state "$stateId" from "${getPathToScope(sourceEvent)}""""
      logger.error(message)
    } else {
      state.set(value, statePath, false)
      event.dependencies.add(state)
    }
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while setting state.\n${e.message}")
  }
}
