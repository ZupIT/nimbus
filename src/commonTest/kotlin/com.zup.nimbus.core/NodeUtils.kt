package com.zup.nimbus.core

import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenNode

object NodeUtils {
  fun triggerEvent(node: ServerDrivenNode?, event: String, implicitStateValue: Any? = null) {
    val action = node?.properties?.get(event)
    if (action is Function<*>) (action as (implicitState: Any?) -> Unit)(implicitStateValue)
  }

  fun pressButton(screen: ServerDrivenNode?, buttonId: String) {
    if (screen == null) return
    if (screen !is RenderNode) throw Error ("Expected a RenderNode")
    val button = screen.findById(buttonId)
    triggerEvent(button, "onPress")
  }
}
