package com.zup.nimbus.core.tree.container

import com.zup.nimbus.core.dependencyGraph.Dependency
import com.zup.nimbus.core.dependencyGraph.Dependent
import com.zup.nimbus.core.tree.stateful.ServerDrivenNode
import com.zup.nimbus.core.tree.stateful.UINode

class NodeContainer(private val nodeList: List<ServerDrivenNode>): Dependent, Dependency() {
  private var uiNodes = emptyList<UINode>()

  init {
    update()
    // this should be updated whenever one of its polymorphic node changes
    nodeList.forEach { if (it !is UINode) it.addDependent(this) }
  }

  fun read(): List<UINode> {
    return uiNodes
  }

  private fun extractUIFromPolymorphicNode(node: ServerDrivenNode): List<UINode> {
    val result = mutableListOf<UINode>()
    node.children?.forEach {
      if (it is UINode) result.add(it)
      else result.addAll(extractUIFromPolymorphicNode(it))
    }
    return result
  }

  private fun extractUIFromNode(node: ServerDrivenNode): List<UINode> {
    return when(node) {
      is UINode -> listOf(node)
      else -> extractUIFromPolymorphicNode(node)
    }
  }

  override fun update() {
    val result = mutableListOf<UINode>()
    nodeList.forEach { result.addAll(extractUIFromNode(it)) }
    uiNodes = result
  }
}
