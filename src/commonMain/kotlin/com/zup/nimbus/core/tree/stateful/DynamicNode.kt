package com.zup.nimbus.core.tree.stateful

import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.tree.container.NodeContainer
import com.zup.nimbus.core.tree.container.PropertyContainer

open class DynamicNode(
  id: String,
  component: String,
  states: List<ServerDrivenState>?,
  parent: ServerDrivenNode?,
  val polymorphic: Boolean = false,
) : ServerDrivenNode(id, component, null, null, states, parent) {
  internal var propertyContainer: PropertyContainer? = null
  internal var childrenContainer: NodeContainer? = null

  internal fun makeDynamic(propertyContainer: PropertyContainer?, childrenContainer: NodeContainer?) {
    this.propertyContainer = propertyContainer
    propertyContainer?.addDependent(this)
    this.childrenContainer = childrenContainer
    childrenContainer?.addDependent(this)
    update()
    hasChanged = false
  }

  override fun update() {
    propertyContainer?.let { properties = it.read() }
    childrenContainer?.let { children = it.read() }
    hasChanged = true
  }
}
