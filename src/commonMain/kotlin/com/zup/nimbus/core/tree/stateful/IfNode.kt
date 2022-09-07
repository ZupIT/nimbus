package com.zup.nimbus.core.tree.stateful

import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.utils.valueOfKey

class IfNode(
  id: String,
  states: List<ServerDrivenState>?,
  parent: ServerDrivenNode,
) : DynamicNode(id, "if", states, parent) {
  override fun update() {
    val condition: Boolean = valueOfKey(propertyContainer?.read(), "condition")
    val fromContainer = childrenContainer?.read()
    val thenNode = fromContainer?.find { it.component == "then" }
    val elseNode = fromContainer?.find { it.component == "else" }
    val previousChildrenStructure = children?.map { it.id }
    children = if (condition) thenNode?.children else elseNode?.children
    hasChanged = previousChildrenStructure != children?.map { it.id }
  }
}
