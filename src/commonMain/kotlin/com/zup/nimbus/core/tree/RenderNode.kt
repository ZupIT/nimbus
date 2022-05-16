package com.zup.nimbus.core.tree

import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.transformJsonObjectToMap
import com.zup.nimbus.core.utils.valueOf
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

class RenderNode(
  override val id: String,
  override val component: String,
  /**
   * Stores the properties of this component after they've been processed, i.e. after the expressions have been resolved
   * and actions deserialized.
   */
  override var properties: Map<String, Any?>?,
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
  companion object {
    /**
     * Creates a RenderNode from a Json string.
     *
     * @param json the json string to deserialize into a RenderNode.
     * @param idManager the idManager to use for generating ids for components without ids.
     * @return the resulting RenderNode.
     * @throws MalformedJsonError if the string is not a valid json.
     * @throws MalformedComponentError when a component node contains unexpected data.
     */
    @Throws(MalformedJsonError::class, MalformedComponentError::class)
    fun fromJsonString(json: String, idManager: IdManager): RenderNode {
      val jsonObject: JsonObject
      try {
        jsonObject = Json.decodeFromString(json)
      } catch (e: Throwable) {
        throw MalformedJsonError("The string provided is not a valid json.")
      }
      return fromJsonObject(jsonObject, idManager)
    }

    /**
     * Creates a RenderNode from a JsonObject.
     * @param jsonObject the json object to deserialize into a RenderNode.
     * @param idManager the idManager to use for generating ids for components without ids.
     * @return the resulting RenderNode.
     * @throws MalformedComponentError when a component node contains unexpected data.
     */
    @Throws(MalformedComponentError::class)
    fun fromJsonObject(jsonObject: JsonObject, idManager: IdManager): RenderNode {
      return fromMap(transformJsonObjectToMap(jsonObject), idManager)
    }

    /**
     * Creates a RenderNode from a Map.
     * @param map the map to deserialize into a RenderNode.
     * @param idManager the idManager to use for generating ids for components without ids.
     * @return the resulting RenderNode.
     * @throws MalformedComponentError when a component node contains unexpected data.
     */
    @Throws(MalformedComponentError::class)
    fun fromMap(map: Map<String, *>, idManager: IdManager, jsonPath: String = "$"): RenderNode {
      val originalId: String? = valueOf(map, "id")
      try {
        return RenderNode(
          id = originalId ?: idManager.next(),
          component = valueOf(map, "_:component"),
          stateId = valueOf(map, "state.id"),
          stateValue = valueOf(map, "state.value"),
          rawProperties = valueOf(map, "properties"),
          children = valueOf<List<Map<String, *>>?>(map, "children")?.mapIndexed { index, value ->
            fromMap(value, idManager, "$jsonPath.children[:$index]")
          },
          stateHierarchy = null,
          properties = null,
        )
      } catch (e: UnexpectedDataTypeError) {
        throw MalformedComponentError(originalId, jsonPath, e.message)
      }
    }

    fun empty(): RenderNode {
      return RenderNode("", "", null, null, null, null, null, null)
    }

    /**
     * Verifies if the given map is a ServerDrivenNode.
     */
    fun isServerDrivenNode(maybeNode: Any?): Boolean {
      return maybeNode is Map<*, *> && maybeNode.containsKey("_:component")
    }
  }

  /**
   * The state declared by this component. Null if it doesn't declare a state.
   */
  val state: ServerDrivenState? = if (stateId == null) null else ServerDrivenState(stateId, stateValue, this)

  /**
   * True if this node has been rendered at least once.
   */
  var isRendered = false

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
      try {
        val children = requireNotNull(parentNode.children)
        if (children.isEmpty()) throw Error()

        val indexToReplace = children.indexOfFirst { child -> child.id == idOfNodeToReplace }
        if (indexToReplace >= 0) {
          val newChildren = children.toMutableList()
          newChildren[indexToReplace] = newNode
          parentNode.children = newChildren
          return parentNode
        }

        for (child in children) {
          val parent = findAndReplaceChild(child, newNode, idOfNodeToReplace)
          if (parent != null) return parent
        }

        return null
      } catch (exception: Throwable) {
        return null
      }
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
    val children = target.children ?: listOf()

    when(mode) {
      TreeUpdateMode.Append -> target.children = children + listOf(newNode)
      TreeUpdateMode.Prepend -> target.children = listOf(newNode) + children
      TreeUpdateMode.Replace -> target.children = listOf(newNode)
      else -> return null
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
    try {
      val children = requireNotNull(this.children)
      if (id.isBlank() || id.isEmpty()) return null

      var i = 0
      var parent: RenderNode? = null
      while (i < children.size && parent == null) {
        val child = children[i]
        parent = if (child.id == id) child else child.findById(id)
        i++
      }

      return parent
    } catch (error: Throwable) {
      return null
    }
  }
}
