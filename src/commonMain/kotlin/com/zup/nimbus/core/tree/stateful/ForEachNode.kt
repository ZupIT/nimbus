package com.zup.nimbus.core.tree.stateful

import com.zup.nimbus.core.ServerDrivenState

class ForEachNode(
  id: String,
  states: List<ServerDrivenState>?,
  parent: ServerDrivenNode,
) : DynamicNode(id, "forEach", states, parent, true) {
  override fun update() {
    TODO("Not yet implemented")
  }
}
