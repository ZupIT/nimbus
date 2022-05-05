package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenAction

data class ActionTriggeredEvent(
  val action: ServerDrivenAction,
  val element: RenderNode,
  val view: ServerDrivenView,
)
