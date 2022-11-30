package br.com.zup.nimbus.core

import br.com.zup.nimbus.core.tree.ServerDrivenEvent
import br.com.zup.nimbus.core.tree.dynamic.node.RootNode
import br.com.zup.nimbus.core.tree.ServerDrivenNode
import br.com.zup.nimbus.core.tree.findNodeById

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

  fun getContent(tree: RootNode): ServerDrivenNode = tree.children?.first()!!
}
