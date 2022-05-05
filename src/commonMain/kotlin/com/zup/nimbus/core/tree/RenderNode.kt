package com.zup.nimbus.core.tree

import com.zup.nimbus.core.utils.then
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
     * Creates a RenderNode from a Json string.
     *
     * @throws MalformedComponentError when a component node contains unexpected data.
     */
    fun fromJsonString(json: String, idManager: IdManager): RenderNode {
      val jsonObject = Json.decodeFromString<JsonObject>(json)
      return fromJsonObject(jsonObject, idManager)
    }

    /**
     * Creates a RenderNode from a JsonObject.
     *
     * @throws MalformedComponentError when a component node contains unexpected data.
     */
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

  /**
   * Replaces the node with id "idOfNodeToReplace" of this tree by "newNode".
   *
   * @param newNode the node to be inserted into the tree.
   * @param idOfNodeToReplace the id of the node to replace with "newNode". If this is the root node or if it doesn't
   * exist, the tree is not altered.
   * @return the node that received "newNode" as a child, i.e. its parent. This will be null if "idOfNodeToReplace"
   * was the root node or if it wasn't found.
   */
  private fun replace(newNode: RenderNode, idOfNodeToReplace: String): RenderNode? {
    if (this.id == idOfNodeToReplace) return null

    fun findAndReplaceChild(parentNode: RenderNode, newNode: RenderNode, idOfNodeToReplace: String,): RenderNode? {
      if (parentNode.children.isNullOrEmpty()) return null

      val indexToReplace = parentNode.children!!.indexOfFirst { child -> child.id == idOfNodeToReplace }
      if (indexToReplace >= 0) {
        val newChildren = parentNode.children!!.toMutableList()
        newChildren[indexToReplace] = newNode
        parentNode.children = newChildren
        return parentNode
      }

      for (child in parentNode.children!!) {
        val parent = findAndReplaceChild(child, newNode, idOfNodeToReplace)
        if (parent != null) return parent
      }

      return null
    }

    return findAndReplaceChild(this, newNode, idOfNodeToReplace)
  }

  /**
   * Inserts "newNode" into the tree by adding it to the node with id "idOfParentNode". The new node will be inserted
   * according to the parameter "mode".
   *
   * @param newNode the node to be inserted into the tree.
   * @param idOfParentNode the id of the node to receive "newNode" as a child. If no node with this id exists, the tree
   * is not altered.
   * @param mode dictates how to insert "newNode" into the node identifies by "idOfParentNode".
   * TreeUpdateMode.ReplaceItself is not acceptable here.
   * @return the node that received "newNode" as one of its child, i.e. its parent, the node identified by
   * "idOfParentNode". If "idOfParentNode" wasn't found in the tree, null is returned.
   */
  private fun insert(newNode: RenderNode, idOfParentNode: String, mode: TreeUpdateMode): RenderNode? {
    val target = if (this.id == idOfParentNode) this else findById(idOfParentNode) ?: return null
    target.children = target.children ?: listOf()

    when(mode) {
      TreeUpdateMode.Append -> target.children = target.children!! + listOf(newNode)
      TreeUpdateMode.Prepend -> target.children = listOf(newNode) + target.children!!
      TreeUpdateMode.Replace -> target.children = listOf(newNode)
      else -> {}
    }

    return target
  }

  /**
   * Inserts "newNode" into the tree. "newNode" can either replace an existing node or be added to its children.
   *
   * If "mode" is "ReplaceItself", "newNode" will replace the node with id "anchor". In this case, "anchor" must not
   * refer to the root node and there must be a node with this id in the tree, otherwise, the tree will be left
   * unchanged and "null" will be returned. In successful operations, the return value is the node that received
   * "newNode" as one of its children, i.e. its parent.
   *
   * If "mode" is "Replace", "Append" or "Prepend", "newNode" will be added as a child of the node identified by
   * "anchor". If there's no node with id "anchor", the tree will be left unchanged and null will be returned. In
   * successful operations, the return value is the node that received "newNode" as one of its children, i.e. its
   * parent, the node identified by "anchor".
   *
   * @param newNode the node to be inserted into the tree.
   * @param anchor the id of the node to replace or receive "newNode" as a child.
   * @param mode dictates how to insert "newNode" into the tree.
   * @return the parent of "newNode" if the operation is successful. Null otherwise.
   */
  fun update(newNode: RenderNode, anchor: String, mode: TreeUpdateMode): RenderNode? {
    return if (mode == TreeUpdateMode.ReplaceItself) replace(newNode, anchor) else insert(newNode, anchor, mode)
  }

  /**
   * Finds the node identified by "id". If there's no such node in the tree, null is returned.
   *
   * @param id the id to look for.
   * @return the node found or null.
   */
  fun findById(id: String): RenderNode? {
    if (id.isBlank() || id.isEmpty() || this.children.isNullOrEmpty()) return null

    var i = 0
    var parent: RenderNode? = null

    while (i < this.children!!.size && parent == null) {
      val child = this.children!![i]
      parent = if (child.id == id) child else child.findById(id)
      i++
    }

    return parent
  }
}
