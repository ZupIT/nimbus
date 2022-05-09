package com.zup.nimbus.core.tree

interface ServerDrivenAction {
  /**
   * Identifies the action to execute. This follows the pattern "namespace:name", where "namespace:" is optional.
   * Actions without a namespace are core actions.
   */
  val action: String
  /**
   * The property map for this action. If this component has no properties, this will be null or an empty map.
   */
  val properties: Map<String, Any?>?
}
