package com.zup.nimbus.core.tree.dynamic.container

import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.dependency.CommonDependency
import com.zup.nimbus.core.dependency.Dependent
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.tree.dynamic.node.DynamicNode

/**
 * Manages a dynamic collection of nodes, updating the values returned by `read()` whenever they update. A DynamicNode
 * can update if it's polymorphic like ForEach and If.
 */
class NodeContainer(
  private val nodeList: List<DynamicNode>,
): Dependent, CommonDependency(), LazilyScoped<NodeContainer> {
  private var uiNodes = emptyList<DynamicNode>()
  private var hasInitialized = false

  override fun initialize(scope: Scope) {
    if (hasInitialized) throw DoubleInitializationError()
    nodeList.forEach {
      it.initialize(scope)
      if (it.polymorphic) it.addDependent(this)
    }
    hasInitialized = true
    update()
    hasChanged = false
  }

  /**
   * Returns the current set of UI Nodes that should be rendered by the UI Layer. This excludes any polymorphic node.
   * An If node for instance (polymorphic), is replaced by its result (the contents of Then or Else).
   */
  fun read(): List<DynamicNode> {
    return uiNodes
  }

  /**
   * Recursively skips a polymorphic node getting its children instead.
   */
  private fun extractUIFromPolymorphicNode(node: DynamicNode): List<DynamicNode> {
    val result = mutableListOf<DynamicNode>()
    node.children?.forEach {
      if (it.polymorphic) result.addAll(extractUIFromPolymorphicNode(it))
      else result.add(it)
    }
    return result
  }

  private fun extractUIFromNode(node: DynamicNode): List<DynamicNode> {
    return if (node.polymorphic) extractUIFromPolymorphicNode(node) else listOf(node)
  }

  override fun update() {
    val result = mutableListOf<DynamicNode>()
    nodeList.forEach { result.addAll(extractUIFromNode(it)) }
    if (result.map { it.id } != uiNodes.map { it.id }) {
      uiNodes = result
      hasChanged = true
    }
  }

  /**
   * Clones this node container adding a suffix to the id of each node contained.
   */
  fun clone(idSuffix: String): NodeContainer {
    if (hasInitialized) throw CloneAfterInitializationError()
    val clonedNodeList = nodeList.map { it.clone(idSuffix) }
    return NodeContainer(clonedNodeList)
  }

  override fun clone(): NodeContainer = clone("")
}
