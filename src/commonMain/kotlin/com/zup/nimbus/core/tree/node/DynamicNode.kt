package com.zup.nimbus.core.tree.node

import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.dependency.CommonDependency
import com.zup.nimbus.core.scope.CommonScope
import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.tree.container.NodeContainer
import com.zup.nimbus.core.tree.container.PropertyContainer

open class DynamicNode(
  override val id: String,
  override val component: String,
  states: List<ServerDrivenState>?,
  val polymorphic: Boolean = false,
): CommonDependency(), Scope by CommonScope(states), LazilyScoped<DynamicNode>, ServerDrivenNode {
  override var properties: Map<String, Any?>? = null
  override var children: List<DynamicNode>? = null
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

  protected fun clone(idSuffix: String, builder: (String, List<ServerDrivenState>?) -> DynamicNode): DynamicNode {
    if (parent != null) throw CloneAfterInitializationError()
    val cloned = builder("$id$idSuffix", states?.map { it.clone() })
    cloned.propertyContainer = propertyContainer?.clone()
    cloned.childrenContainer = childrenContainer?.clone(idSuffix)
    return cloned
  }

  open fun clone(idSuffix: String): DynamicNode = clone(idSuffix) { id, states ->
    DynamicNode(id, component, states)
  }

  override fun clone(): DynamicNode = clone("")
}
