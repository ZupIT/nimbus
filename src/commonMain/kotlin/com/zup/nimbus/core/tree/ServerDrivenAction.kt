package com.zup.nimbus.core.tree

import com.zup.nimbus.core.ActionHandler
import com.zup.nimbus.core.dependencyGraph.Dependent
import com.zup.nimbus.core.tree.stateful.ServerDrivenEvent

interface ServerDrivenAction: Dependent {
  /**
   * Identifies the action to execute. This follows the pattern "namespace:name", where "namespace:" is optional.
   * Actions without a namespace are core actions.
   */
  val name: String
  val handler: ActionHandler

  /**
   * The property map for this action. If this component has no properties, this will be null or an empty map.
   */
  val properties: Map<String, Any?>?

  /**
   * Metadata for this action. The metadata should not make a difference when executing the action, but can contain
   * useful data for action observers. Metadata properties are not specific to an action, they usually appear on
   * any action no matter its name. Example: for creating an analytics service, one might add the metadata "analytics"
   * to decide whether or not to generate a record for the action. Any kind of action would be able to receive the
   * metadata "analytics".
   */
  val metadata: Map<String, Any?>?
  val event: ServerDrivenEvent
}
