package com.zup.nimbus.core.tree.dynamic.builder

import com.zup.nimbus.core.ActionHandler
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.tree.dynamic.DynamicAction
import com.zup.nimbus.core.tree.dynamic.container.PropertyContainer
import com.zup.nimbus.core.utils.valueOfKey

/**
 * Builds DynamicActions from JSON sources.
 */
class ActionBuilder(private val nimbus: Nimbus) {
  /**
   * Verifies if this JSON structure represents an action.
   */
  fun isJsonAction(maybeAction: Any?): Boolean {
    return maybeAction is Map<*, *> && maybeAction.containsKey("_:action")
  }

  private fun actionNotFoundError(name: String): ActionHandler {
    val error = "Couldn't find handler for action with name \"$name\". Please, make sure you registered your custom actions."
    nimbus.logger.error(error)
    return { nimbus.logger.error(error) }
  }

  private fun createHandler(name: String): ActionHandler {
    val executionHandler = nimbus.uiLibraryManager.getAction(name) ?: return actionNotFoundError(name)
    val observers = nimbus.uiLibraryManager.getActionObservers()
    return { event ->
      executionHandler(event)
      observers.forEach { it(event) }
    }
  }

  /**
   * Builds a DynamicAction from its json map representation.
   *
   * To avoid errors, verify if `map` is an action (isJsonAction) before calling this method.
   */
  fun buildFromJsonMap(map: Map<String, Any?>): DynamicAction {
    val name: String = valueOfKey(map, "_:action")
    val handler = createHandler(name)
    val initHandler = nimbus.uiLibraryManager.getActionInitializer(name)
    val properties: Map<String, Any?>? = valueOfKey(map, "properties")
    val metadata: Map<String, Any?>? = valueOfKey(map, "metadata")
    val action = DynamicAction(name, handler, initHandler)

    action.propertyContainer = properties?.let {
      PropertyContainer(it, nimbus)
    }

    action.metadataContainer = metadata?.let {
      PropertyContainer(it, nimbus)
    }

    return action
  }
}
