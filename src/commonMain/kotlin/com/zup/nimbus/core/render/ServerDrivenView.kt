package com.zup.nimbus.core.render

import com.zup.nimbus.core.action.ServerDrivenNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.tree.ServerDrivenNode

typealias Listener = (tree: ServerDrivenNode) -> Unit

class ServerDrivenView(
  val nimbusInstance: Nimbus,
  val parentNavigator: ServerDrivenNavigator,
) {
  private var current: ServerDrivenNode? = null
  private val listeners: ArrayList<Listener> = ArrayList()
  val renderer = Renderer(
    view = this,
    onTakeSnapshot = { current = it },
    onFinish = { runListeners(it) },
  )

  private fun runListeners(tree: ServerDrivenNode) {
    listeners.forEach { it(tree) }
  }

  fun getCurrentTree(): ServerDrivenNode? {
    return current
  }

  fun onChange(listener: Listener): () -> Unit {
    listeners.add(listener)
    return { listeners.remove(listener) }
  }
}
