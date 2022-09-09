package com.zup.nimbus.core.tree.container

import com.zup.nimbus.core.dependencyGraph.Dependency
import com.zup.nimbus.core.dependencyGraph.Dependent
import com.zup.nimbus.core.tree.stateful.DynamicNode
import com.zup.nimbus.core.tree.stateful.ServerDrivenNode

class NodeContainer(private val nodeList: List<ServerDrivenNode>): Dependent, Dependency() {
  private var uiNodes = emptyList<ServerDrivenNode>()

  init {
    update()
    hasChanged = false
    // this should be updated whenever one of its polymorphic node changes
    nodeList.forEach { if (isPolymorphic(it)) it.addDependent(this) }
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
}
