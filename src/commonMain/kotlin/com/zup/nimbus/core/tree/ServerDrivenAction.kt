package com.zup.nimbus.core.tree

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
          it as Map<*, *>
          ServerDrivenAction(
            action = it["action"] as String,
            properties = it["properties"] as Map<String, Any>?,
          )
        }
      } catch (e: Exception) {
        throw MalformedActionListError()
      }
    }
  }
}


