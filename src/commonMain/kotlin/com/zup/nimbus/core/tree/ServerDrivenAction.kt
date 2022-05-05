package com.zup.nimbus.core.tree

import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOf

data class ServerDrivenAction(
  val action: String,
  val properties: Map<String, Any>?,
) {
  companion object {
    private fun isAction(maybeAction: Any): Boolean {
      return maybeAction is Map<*, *>
        && (
        (maybeAction.keys.size == 1 && maybeAction["action"] is String)
          || (maybeAction.keys.size == 2 && maybeAction["action"] is String && maybeAction["properties"] is Map<*, *>)
        )
    }

    fun isActionList(maybeActionList: List<*>): Boolean {
      return maybeActionList.isNotEmpty() && isAction(maybeActionList.first()!!)
    }

    fun createActionList(actions: List<*>): List<ServerDrivenAction> {
      try {
        return actions.map {
          ServerDrivenAction(
            action = valueOf(it, "action"),
            properties = valueOf(it, "properties"),
          )
        }
      } catch (e: UnexpectedDataTypeError) {
        throw MalformedActionListError(e.message)
      }
    }
  }
}


