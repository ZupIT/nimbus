package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.RenderAction
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenState

private fun resolveActionPropertiesAndMetadata(
  action: RenderAction,
  states: List<ServerDrivenState>,
  resolve: (value: Any?, key: String, extraStates: List<ServerDrivenState>) -> Any?,
) {
  fun mapToResolved(entry: Map.Entry<String, Any?>): Any? {
    /* we should never deserialize ServerDrivenNodes (components) within actions. This would cause all their
    actions to run in the context of this node, which is wrong, they should be run in the context of their own
    parent node. */
    return if (entry.value is Map<*, *> && RenderNode.isServerDrivenNode(entry.value as Map<*, *>)) entry.value
    else resolve(entry.value, entry.key, states)
  }

  action.properties = action.rawProperties?.mapValues { mapToResolved(it) }
  action.metadata = action.rawMetadata?.mapValues { mapToResolved(it) }
}

/**
 * Deserializes a list of ServerDrivenAction into a function.
 *
 * When an action is deserialized, if there's any onActionRendered handler for it, it's run.
 *
 * @param actionList the list of actions to parse into a function.
 * @param event name of the event that triggers the actionList, i.e. the key of the map entry. This will act as the id
 * of the implicit state (if any).
 * @param node the node that declared the actionList.
 * @param view the view holding the node.
 * @param extraStates states that should be accounted even though they are not part of the node's state hierarchy. Use
 * this to deal with implicit states. The extra states must be in descending order of priority and they all have higher
 * priority than `node.stateHierarchy`. This is used when both the parent action and sub action declare implicit
 * states).
 * @param resolve a function to parse all sub-actions and expressions.
 */
internal fun deserializeActions(
  actionList: List<RenderAction>,
  event: String,
  node: RenderNode,
  view: ServerDrivenView,
  extraStates: List<ServerDrivenState>,
  resolve: (value: Any?, key: String, extraStates: List<ServerDrivenState>) -> Any?,
): (implicitContextValue: Any?) -> Unit {
  if (!node.isRendered) {
    val missingHandlers = ArrayList<String>()
    actionList.forEach { action ->
      val onRenderedHandler = view.nimbusInstance.onActionRendered[action.action]
      val executionHandler = view.nimbusInstance.actions[action.action]
      if (onRenderedHandler != null) {
        resolveActionPropertiesAndMetadata(action, emptyList(), resolve)
        onRenderedHandler(ActionEvent(action, event, node, view))
      }
      if (executionHandler == null) missingHandlers.add(action.action)
    }
    if (missingHandlers.isNotEmpty()) {
      view.nimbusInstance.logger.warn(
        "The following actions used in component with id ${node.id} don't have any associated " +
          "handler: ${missingHandlers.distinct().joinToString(", ")}"
      )
    }
  }

  return { implicitStateValue ->
    actionList.forEach { action ->
      val handler = view.nimbusInstance.actions[action.action]
      if (handler == null) {
        view.nimbusInstance.logger.error(
          """Action with name "${action.action}" has been triggered, but no associated handler has been found.""",
        )
      } else {
        val newExtraStates =
          if (implicitStateValue == null) extraStates
          else listOf(ServerDrivenState(event, implicitStateValue, node)) + extraStates
        resolveActionPropertiesAndMetadata(action, newExtraStates, resolve)
        val actionEvent = ActionEvent(action, event, node, view)
        handler(actionEvent)
        view.nimbusInstance.actionObservers.forEach { it(actionEvent) }
      }
    }
  }
}
