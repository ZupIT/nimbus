package com.zup.nimbus.core.render

import com.zup.nimbus.core.action.ServerDrivenNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenNode

typealias Listener = (tree: ServerDrivenNode) -> Unit

class ServerDrivenView(
  val nimbusInstance: Nimbus,
  val parentNavigator: ServerDrivenNavigator,
) {
  private var current: RenderNode? = null
  private val listeners: ArrayList<Listener> = ArrayList()
  internal val renderer = Renderer(
    view = this,
    getCurrentTree = { current },
    replaceCurrentTree = { current = it },
    onFinish = { runListeners() },
  )

  private fun runListeners() {
    listeners.forEach { it(current ?: throw EmptyViewError()) }
  }

  fun onChange(listener: Listener): () -> Unit {
    listeners.add(listener)
    return { listeners.remove(listener) }
  }
}
