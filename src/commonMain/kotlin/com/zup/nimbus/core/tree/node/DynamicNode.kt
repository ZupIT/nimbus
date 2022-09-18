package com.zup.nimbus.core.tree.node

import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.tree.builder.NodeBuilder
import com.zup.nimbus.core.tree.container.NodeContainer
import com.zup.nimbus.core.tree.container.PropertyContainer

open class DynamicNode(
  id: String,
  component: String,
  states: List<ServerDrivenState>?,
  val polymorphic: Boolean = false,
) : ServerDrivenNode(id, component, null, null, states) {
  internal var propertyContainer: PropertyContainer? = null
  internal var childrenContainer: NodeContainer? = null

  override fun update() {
    propertyContainer?.let { properties = it.read() }
    childrenContainer?.let { children = it.read() }
    hasChanged = true
  }

  override fun initialize(scope: Scope) {
    if (parent != null) throw DoubleInitializationError()
    parent = scope
    propertyContainer?.initialize(this)
    childrenContainer?.initialize(this)
    propertyContainer?.addDependent(this)
    childrenContainer?.addDependent(this)
    update()
    hasChanged = false
  }

  protected fun clone(idSuffix: String, builder: (String, List<ServerDrivenState>?) -> DynamicNode): ServerDrivenNode {
    if (parent != null) throw CloneAfterInitializationError()
    val cloned = builder("$id$idSuffix", states?.map { it.clone() })
    cloned.propertyContainer = propertyContainer?.clone()
    cloned.childrenContainer = childrenContainer?.clone(idSuffix)
    return cloned
  }

  override fun clone(idSuffix: String): ServerDrivenNode = clone(idSuffix) { id, states ->
    DynamicNode(id, component, states)
  }
}
