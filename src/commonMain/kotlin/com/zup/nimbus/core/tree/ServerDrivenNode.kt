package com.zup.nimbus.core.tree

interface ServerDrivenNode {
  /**
   * The unique id for this component.
   */
  val id: String
  /**
   * Identifies the component to render. This follows the pattern "namespace:name", where "namespace:" is optional.
   * Components without a namespace are core components.
   */
  val component: String
  /**
   * The property map for this component. If this component has no properties, this will be null or an empty map.
   */
  val properties: Map<String, Any?>?
  /**
   * The children of this node. If this is a leaf-node, children will be null or an empty map.
   */
  val children: List<ServerDrivenNode>?
  /**
   * Whether this node needs to be updated or not. If the UI layer decides to use this, it must set it to false once
   * the node is rendered.
   */
  var dirty: Boolean
}
