package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenAction

fun deserializeActions(
  actionList: List<ServerDrivenAction>,
  node: RenderNode,
  view: ServerDrivenView,
): () -> Unit {
  return {
    actionList.forEach {
      val handler = view.nimbusInstance.actions[it.action]
      if (handler == null) {
        view.nimbusInstance.logger.error(
          """Action with name "${it.action}" has been triggered, but no associated handler has been found.""",
        )
      } else {
        handler(ActionTriggeredEvent(it, node, view))
      }
    }
  }
}
