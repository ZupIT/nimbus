package com.zup.nimbus.core.tree.stateful

import com.zup.nimbus.core.ServerDrivenState

/**
 * UI nodes are ServerDrivenNodes that must be rendered. Every UINode is received by the UI layer and ends up in the
 * final UI tree.
 *
 * Every ServerDrivenNode that is not a UI Node is considered to be polymorphic. A polymorphic node doesn't know
 * exactly which UI element it will turn out to be and will never be received by the UI Layer. The elements a
 * polymorphic node will turn out to represent will depend on a value, which will normally be controlled by a state.
 * For instance, the components "if" and "forEach" are polymorphic nodes because they won't be actually rendered, what
 * will be rendered instead is their children, which might change depending on the current value of "condition" or
 * "items", respectively.
 */
open class UINode(
  id: String,
  component: String,
  states: List<ServerDrivenState>?,
  parent: ServerDrivenNode?,
) : DynamicNode(id, component, states, parent) {
  override fun update() {
    propertyContainer?.let { properties = it.read() }
    childrenContainer?.let { children = it.read() }
    hasChanged = true
  }
}
