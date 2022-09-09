package com.zup.nimbus.core.tree.builder

import com.zup.nimbus.core.ActionHandler
import com.zup.nimbus.core.ActionInitializedEvent
import com.zup.nimbus.core.scope.EventScope
import com.zup.nimbus.core.tree.DynamicAction
import com.zup.nimbus.core.tree.ServerDrivenAction
import com.zup.nimbus.core.tree.container.PropertyContainer
import com.zup.nimbus.core.utils.valueOfKey

object ActionBuilder {
  fun isJsonAction(maybeAction: Any?): Boolean {
    return maybeAction is Map<*, *> && maybeAction.containsKey("_:action")
  }

  private fun actionNotFoundError(name: String, scope: EventScope): ActionHandler {
    val error = "Couldn't find handler for action with name \"$name\". Please, make sure you registered your custom actions."
    scope.getLogger().error(error)
    return { scope.getLogger().error(error) }
  }

  private fun createHandler(name: String, scope: EventScope): ActionHandler {
    val executionHandler = scope.getUILibraryManager().getAction(name) ?: return actionNotFoundError(name, scope)
    val observers = scope.getUILibraryManager().getActionObservers()
    return { event ->
      executionHandler(event)
      observers.forEach { it(event) }
    }
  }

  fun fromJsonAction(map: Map<String, Any?>, scope: EventScope): ServerDrivenAction {
    val event = scope.getEvent()
    val name: String = valueOfKey(map, "_:action")
    val handler = createHandler(name, scope)
    val initHandler = scope.getUILibraryManager().getActionInitializer(name)
    val properties: Map<String, Any?>? = valueOfKey(map, "properties")
    val metadata: Map<String, Any?>? = valueOfKey(map, "metadata")
    val action = DynamicAction(name, handler)

    if (properties != null) {
      action.setPropertyContainer(PropertyContainer(properties, event, scope))
    }

    if (metadata != null) {
      action.setMetadataContainer(PropertyContainer(metadata, event, scope))
    }

    initHandler?.let { it(ActionInitializedEvent(action, scope)) }
    return action
  }
}
