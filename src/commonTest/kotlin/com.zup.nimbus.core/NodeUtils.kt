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
    val button = screen.findById(buttonId) ?: throw Error("Could not find button with id $buttonId")
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

  /**
   * runs findById on children, not on rawChildren
   */
  fun findById(node: ServerDrivenNode, id: String): ServerDrivenNode? {
    if (node.id == id) return node
    node.children?.forEach {
      val result = findById(it, id)
      if (result != null) return result
    }
    return null
  }
}
