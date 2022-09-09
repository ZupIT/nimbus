package com.zup.nimbus.core.ui.action

import com.zup.nimbus.core.regex.toFastRegex
import com.zup.nimbus.core.ActionTriggeredEvent
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
  val sourceEvent = event.scope.getEvent()
  try {
    val path: String = valueOfKey(properties, "path")
    val value: Any? = valueOfKey(properties, "value")
    val matchResult = statePathRegex.findWithGroups(path) ?:
      return event.scope.getLogger().error("""The path "$path" is not a valid state path.""")
    val (stateId, statePath) = matchResult.destructured
    val state = sourceEvent.find(stateId)
    if (state == null) {
      val message = """Could not find state "$stateId" from "${getPathToOrigin(sourceEvent)}""""
      event.scope.getLogger().error(message)
    } else {
      state.set(value, statePath, false)
      event.dependencies.add(state)
    }
  } catch (e: UnexpectedDataTypeError) {
    event.scope.getLogger().error("Error while setting state.\n${e.message}")
  }
}
