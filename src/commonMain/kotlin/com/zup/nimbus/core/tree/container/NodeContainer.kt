package com.zup.nimbus.core.tree.container

import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.dependency.Dependency
import com.zup.nimbus.core.dependency.Dependent
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.tree.node.DynamicNode
import com.zup.nimbus.core.tree.node.ServerDrivenNode

class NodeContainer(
  private val nodeList: List<ServerDrivenNode>,
): Dependent, Dependency(), LazilyScoped<NodeContainer> {
  private var uiNodes = emptyList<ServerDrivenNode>()
  private var hasInitialized = false

  override fun initialize(scope: Scope) {
    if (hasInitialized) throw DoubleInitializationError()
    nodeList.forEach {
      it.initialize(scope)
      if (isPolymorphic(it)) it.addDependent(this)
    }
    hasInitialized = true
    update()
    hasChanged = false
  }

  private fun isPolymorphic(node: ServerDrivenNode): Boolean {
    return node is DynamicNode && node.polymorphic
  }

  fun read(): List<ServerDrivenNode> {
    return uiNodes
  }

  private fun extractUIFromPolymorphicNode(node: ServerDrivenNode): List<ServerDrivenNode> {
    val result = mutableListOf<ServerDrivenNode>()
    node.children?.forEach {
      if (isPolymorphic(it)) result.addAll(extractUIFromPolymorphicNode(it))
      else result.add(it)
    }
    return result
  }

  private fun extractUIFromNode(node: ServerDrivenNode): List<ServerDrivenNode> {
    return if (isPolymorphic(node)) extractUIFromPolymorphicNode(node) else listOf(node)
  }

  override fun update() {
    val result = mutableListOf<ServerDrivenNode>()
    nodeList.forEach { result.addAll(extractUIFromNode(it)) }
    if (result.map { it.id } != uiNodes.map { it.id }) {
      uiNodes = result
      hasChanged = true
    }
  }

  fun clone(idSuffix: String): NodeContainer {
    if (hasInitialized) throw CloneAfterInitializationError()
    val clonedNodeList = nodeList.map { it.clone(idSuffix) }
    return NodeContainer(clonedNodeList)
  }

  override fun clone(): NodeContainer = clone("")
}
