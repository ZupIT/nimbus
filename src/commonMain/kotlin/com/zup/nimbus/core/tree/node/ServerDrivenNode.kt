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
  /*val view: ServerDrivenView by lazy {
    when (parent) {
      is ServerDrivenView -> parent
      is ServerDrivenNode -> parent.view
      else -> throw IllegalStateException(
        "This node is not yet linked to a view. If you need the nimbus instance, access the property nimbus instead."
      )
    }
  }

  val nimbus: Nimbus by lazy {
    when (parent) {
      // it is important that it doesn't rely in the property "view" for getting the nimbus instance because most of
      // the times, "nimbus" will be available and accessed much before "view".
      is Nimbus -> parent
      is ServerDrivenView -> parent.nimbus
      is ServerDrivenNode -> parent.nimbus
      else -> throw IllegalStateException("This node is not linked to a nimbus instance!")
    }
  }*/

  fun findNodeById(id: String): ServerDrivenNode? {
    if (this.id == id) return this
    this.children?.forEach {
      val found = it.findNodeById(id)
      if (found != null) return found
    }
    return null
  }
}
