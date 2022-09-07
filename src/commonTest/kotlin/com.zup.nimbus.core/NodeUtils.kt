package com.zup.nimbus.core

import com.zup.nimbus.core.tree.stateful.ServerDrivenNode
import com.zup.nimbus.core.tree.stateful.findNodeById

object NodeUtils {
  fun triggerEvent(node: ServerDrivenNode?, event: String, implicitStateValue: Any? = null) {
    val action = node?.properties?.get(event)
    if (action is Function<*>) (action as (implicitState: Any?) -> Unit)(implicitStateValue)
  }

  fun pressButton(screen: ServerDrivenNode?, buttonId: String) {
    if (screen == null) return
    val button = screen.findNodeById(buttonId) ?: throw Error("Could not find button with id $buttonId")
    triggerEvent(button, "onPress")
  }

  // transforms a tree of nodes into a list of nodes (Depth First Search)
  fun flatten(tree: ServerDrivenNode?): List<ServerDrivenNode> {
    val result = ArrayList<ServerDrivenNode>()
    if (tree != null) {
      result.add(tree)
      tree.children?.forEach { result.addAll(flatten(it)) }
    }
    return result
  }
}
