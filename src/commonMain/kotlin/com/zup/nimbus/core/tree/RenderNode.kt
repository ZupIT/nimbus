package com.zup.nimbus.core.tree

import com.zup.nimbus.core.utils.transformJsonElementToKotlinType
import com.zup.nimbus.core.utils.transformJsonObjectToMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

class RenderNode(
  override val id: String,
  override val component: String,
  /**
   * Stores the properties of this component after they've been processed, i.e. after the expressions have been resolved
   * and actions deserialized.
   */
  override var properties: MutableMap<String, Any?>?,
  override var children: List<RenderNode>?,
  /**
   * Stores the original properties of this component, before any processing. This can contain expressions in their
   * string form and actions in their Object form (Action).
   */
  var rawProperties: MutableMap<String, Any?>?,
  /**
   * All states accessible by this component in descending order of priority, i.e. the first element will be the state
   * of highest priority and the last element will be the state of lowest priority.
   *
   * stateHierarchy will be null if this node has not yet been processed by the renderer.
   */
  var stateHierarchy: List<ServerDrivenState>?,
  stateId: String?,
  stateValue: Any?,
): ServerDrivenNode {
  companion object Factory {
    /**
     * STOPPED HERE!
     */
    fun fromJsonString(json: String, idManager: IdManager): RenderNode {
      val jsonObject = Json.decodeFromString<JsonObject>(json)
      return fromJsonObject(jsonObject, idManager)
    }

    fun fromJsonObject(jsonObject: JsonObject, idManager: IdManager): RenderNode {
      var stateId: String? = null
      var stateValue: Any? = null
      val stateMap = if (jsonObject.containsKey("state")) jsonObject["state"] else null
      if (stateMap != null) {
        stateId = stateMap.jsonObject["id"]?.jsonPrimitive?.content ?: throw MalformedComponentError()
        stateValue = stateMap.jsonObject["value"]?.let { transformJsonElementToKotlinType(it) }
      }

      val children = jsonObject["children"]?.jsonArray?.map { fromJsonObject(it.jsonObject, idManager) }

      return RenderNode(
        id = jsonObject["id"]?.jsonPrimitive?.content ?: idManager.next(),
        component = jsonObject["component"]?.jsonPrimitive?.content ?: throw MalformedComponentError(),
        stateId = stateId,
        stateValue = stateValue,
        // fixme: validate json structure
        rawProperties = if (jsonObject.containsKey("properties")) transformJsonObjectToMap(
          jsonObject["properties"]?.jsonObject ?: throw MalformedComponentError()
        ) else null,
        children = children,
        stateHierarchy = null,
        properties = null,
      )
    }
  }

  /**
   * The state declared by this component. Null if it doesn't declare a state.
   */
  val state: ServerDrivenState? = if (stateId == null) null else ServerDrivenState(stateId, stateValue, this)

  private fun requireInsertionMode(mode: TreeUpdateMode) {
    require(mode in listOf(TreeUpdateMode.Prepend, TreeUpdateMode.Append, TreeUpdateMode.Replace)) {
      "Update mode must be Append, Prepend or Replace"
    }
  }

  // returns the parent holding the previous node that has been replaced. Returns null if no node has been found to
  // replace.
  private fun replace(node: RenderNode, anchor: String): RenderNode? {
    throw Error("Not Implemented yet!")
  }

  // returns the RenderNode where "node" has been inserted into, i.e. the node indicated by "anchor". Null if the node
  // hasn't been found.
  private fun insert(node: RenderNode, anchor: String, mode: TreeUpdateMode): RenderNode? {
    requireInsertionMode(mode)
    throw Error("Not Implemented yet!")
  }

  fun update(node: RenderNode, anchor: String, mode: TreeUpdateMode): RenderNode? {
    return if (mode == TreeUpdateMode.ReplaceItself) replace(node, anchor) else insert(node, anchor, mode)
  }

  fun findById(id: String): RenderNode? {
    throw Error("Not Implemented yet!")
  }
}
