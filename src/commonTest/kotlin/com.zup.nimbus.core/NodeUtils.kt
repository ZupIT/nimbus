package com.zup.nimbus.core

import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.tree.node.RootNode
import com.zup.nimbus.core.tree.node.ServerDrivenNode

object NodeUtils {
  fun triggerEvent(node: ServerDrivenNode?, eventName: String, implicitStateValue: Any? = null) {
    if (node == null) throw IllegalArgumentException("The node is null, can't trigger event")
    val event = node.properties?.get(eventName)
    if (event is ServerDrivenEvent) event.run(implicitStateValue)
    else throw IllegalArgumentException("The event name \"$eventName\" does not correspond to an existing event.")
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

  fun getContent(tree: RootNode): ServerDrivenNode = tree.children?.first()!!
}
