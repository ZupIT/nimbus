package com.zup.nimbus.core.tree.node

import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.utils.valueOfKey

// fixme: normally, in UI frameworks, if-else blocks completely remove the other branch from the tree and rebuilds it
//  when the condition changes. This is not being done here. On the positive side, states will never be lost when
//  switching from true to false. On the other hand, we won't free up the memory for the if-else branch not currently
//  rendered until the associated RootNode is unmounted. If we decide this to be a feature and not a bug, remove this
//  commentary.
class IfNode(
  id: String,
  states: List<ServerDrivenState>?,
) : DynamicNode(id, "if", states, true) {
  override fun update() {
    val condition: Boolean = valueOfKey(propertyContainer?.read(), "condition")
    val fromContainer = childrenContainer?.read()
    val thenNode = fromContainer?.find { it.component == "then" }
    val elseNode = fromContainer?.find { it.component == "else" }
    val previousChildrenStructure = children?.map { it.id }
    children = if (condition) thenNode?.children else elseNode?.children
    hasChanged = previousChildrenStructure != children?.map { it.id }
  }

  override fun clone(idSuffix: String): DynamicNode = clone(idSuffix) { id, states -> IfNode(id, states) }
}
