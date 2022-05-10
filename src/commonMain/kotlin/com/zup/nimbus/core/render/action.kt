package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.RenderAction
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenState

/**
 * Deserializes a list of ServerDrivenAction into a function.
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
  return { implicitContextValue ->
    actionList.forEach { action ->
      val handler = view.nimbusInstance.actions[action.action]
      if (handler == null) {
        view.nimbusInstance.logger.error(
          """Action with name "${action.action}" has been triggered, but no associated handler has been found.""",
        )
      } else {
        val newExtraStates =
          if (implicitContextValue == null) extraStates
          else listOf(ServerDrivenState(event, implicitContextValue, node)) + extraStates
        action.properties = action.rawProperties?.mapValues {
          // we should never deserialize ServerDrivenNodes (components) within actions
          if (it.value is Map<*, *> && RenderNode.isServerDrivenNode(it.value as Map<*, *>)) it.value
          else resolve(it.value, it.key, newExtraStates)
          resolve(it.value, it.key, newExtraStates)
        }
        handler(ActionTriggeredEvent(action, node, view))
      }
    }
  }
}
