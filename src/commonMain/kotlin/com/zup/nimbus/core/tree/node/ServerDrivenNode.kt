package com.zup.nimbus.core.tree.node

import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.dependency.Dependency
import com.zup.nimbus.core.dependency.Dependent
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.scope.CommonScope
import com.zup.nimbus.core.scope.Scope

abstract class ServerDrivenNode(
  /**
   * The unique id for this component.
   */
  val id: String,
  /**
   * Identifies the component to render. This follows the pattern "namespace:name", where "namespace:" is optional.
   * Components without a namespace are core components.
   */
  val component: String,
  /**
   * The property map for this component. If this component has no properties, this will be null or an empty map.
   */
  var properties: Map<String, Any?>?,
  /**
   * The children of this node. If this is a leaf-node, children will be null or an empty map.
   */
  var children: List<ServerDrivenNode>?,
  states: List<ServerDrivenState>?,
): Dependency(), Dependent, LazilyScoped<ServerDrivenNode>, Scope by CommonScope(states) {
  fun findNodeById(id: String): ServerDrivenNode? {
    if (this.id == id) return this
    this.children?.forEach {
      val found = it.findNodeById(id)
      if (found != null) return found
    }
    return null
  }

  abstract fun clone(idSuffix: String): ServerDrivenNode

  override fun clone() = clone("")
}
