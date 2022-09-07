package com.zup.nimbus.core.tree.builder

import com.zup.nimbus.core.ActionHandler
import com.zup.nimbus.core.ActionInitializedEvent
import com.zup.nimbus.core.ActionInitializedHandler
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.ast.ExpressionParser
import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.tree.DynamicAction
import com.zup.nimbus.core.tree.ServerDrivenAction
import com.zup.nimbus.core.tree.container.PropertyContainer
import com.zup.nimbus.core.tree.stateful.ServerDrivenEvent
import com.zup.nimbus.core.utils.valueOfKey

class ActionBuilder(
  private val actionHandlers: Map<String, ActionHandler>,
  private val actionInitHandlers: Map<String, ActionInitializedHandler>,
  private val actionObservers: List<ActionHandler>,
  private val expressionParser: ExpressionParser,
  private val logger: Logger,
) {
  companion object {
    fun isJsonAction(maybeAction: Any?): Boolean {
      return maybeAction is Map<*, *> && maybeAction.containsKey("_:action")
    }
  }

  private fun actionNotFoundError(name: String): ActionHandler {
    val error = "Couldn't find handler for action with name \"$name\". Please, make sure you registered your custom actions."
    logger.error(error)
    return { logger.error(error) }
  }

  private fun createHandler(name: String): ActionHandler {
    val executionHandler = actionHandlers[name] ?: return actionNotFoundError(name)
    return { event ->
      executionHandler(event)
      actionObservers.forEach { it(event) }
    }
  }

  fun fromJsonAction(map: Map<String, Any?>, event: ServerDrivenEvent): ServerDrivenAction {
    val name: String = valueOfKey(map, "_:action")
    val handler = createHandler(name)
    val initHandler = actionInitHandlers[name]
    val properties: Map<String, Any?>? = valueOfKey(map, "properties")
    val metadata: Map<String, Any?>? = valueOfKey(map, "metadata")
    val action = DynamicAction(name, handler, event)

    if (properties != null) {
      action.setPropertyContainer(PropertyContainer(expressionParser, this, event.view, properties, event))
    }

    if (metadata != null) {
      action.setMetadataContainer(PropertyContainer(expressionParser, this, event.view, metadata, event))
    }

    initHandler?.let { it(ActionInitializedEvent(action, event)) }
    return action
  }
}
