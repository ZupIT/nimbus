package com.zup.nimbus.core.tree

import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOf

data class RenderAction(
  override val action: String,
  /**
   * Stores the properties of this action after they've been processed, i.e. after the expressions have been resolved
   * and actions deserialized.
   */
  override var properties: Map<String, Any?>?,
  /**
   * Stores the original properties of this action, before any processing. This can contain expressions in their
   * string form and actions in their Object form (Action).
   */
  val rawProperties: Map<String, Any?>?,
  /**
   * Stores the original metadata of this action, before any processing. This can contain expressions in their
   * string form.
   */
  val rawMetadata: Map<String, Any?>?,
  /**
   * Stores the metadata of this action after it's been processed, i.e. after the expressions have been resolved.
   */
  override var metadata: Map<String, Any?>?,
): ServerDrivenAction {
  companion object {
    private fun isAction(maybeAction: Any?): Boolean {
      return maybeAction is Map<*, *> && maybeAction.containsKey("_:action")
    }

    fun isActionList(maybeActionList: List<*>): Boolean {
      return maybeActionList.isNotEmpty() && isAction(maybeActionList.first()!!)
    }

    fun createActionList(actions: List<*>): List<RenderAction> {
      try {
        return actions.map {
          RenderAction(
            action = valueOf(it, "_:action"),
            rawProperties = valueOf(it, "properties"),
            rawMetadata = valueOf(it, "metadata"),
            properties = null,
            metadata = null,
          )
        }
      } catch (e: UnexpectedDataTypeError) {
        throw MalformedActionListError(e.message)
      }
    }
  }
}


