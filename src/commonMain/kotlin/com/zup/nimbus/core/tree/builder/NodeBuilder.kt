package com.zup.nimbus.core.tree.builder

import com.zup.nimbus.core.RawJsonMap
import com.zup.nimbus.core.tree.MalformedComponentError
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.scope.ViewScope
import com.zup.nimbus.core.tree.container.NodeContainer
import com.zup.nimbus.core.tree.container.PropertyContainer
import com.zup.nimbus.core.tree.stateful.ForEachNode
import com.zup.nimbus.core.tree.stateful.IfNode
import com.zup.nimbus.core.tree.stateful.RootNode
import com.zup.nimbus.core.tree.stateful.ServerDrivenNode
import com.zup.nimbus.core.tree.stateful.UINode
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOfKey

object NodeBuilder {
  private fun buildNode(
    jsonNode: RawJsonMap,
    parent: ServerDrivenNode,
    jsonPath: String,
    scope: ViewScope,
  ): ServerDrivenNode {
    val originalId: String? = valueOfKey(jsonNode, "id")
    try {
      val id = originalId ?: scope.getIdManager().next()
      val component: String = valueOfKey(jsonNode, "_:component")
      val stateMap: Map<String, Any>? = valueOfKey(jsonNode, "state")
      val states = stateMap?.let {
        val stateId: String = valueOfKey(stateMap, "id")
        val stateValue: Any? = valueOfKey(stateMap, "value")
        listOf(ServerDrivenState(stateId, stateValue))
      }
      val properties: Map<String, Any?>? = valueOfKey(jsonNode, "properties")
      val children: List<Map<String, *>>? = valueOfKey(jsonNode, "children")

      val node = when(component) {
        "if" -> IfNode(id, states, parent)
        "forEach" -> ForEachNode(id, states, parent)
        else -> UINode(id, component, states, parent)
      }

      val propertyContainer = properties?.let {
        PropertyContainer(properties, node, scope)
      }

      val childrenContainer = children?.let {
        val childrenAsNodes = children.mapIndexed { index, item ->
          buildNode(item, node, "$jsonPath.children[:$index]", scope)
        }
        NodeContainer(childrenAsNodes)
      }

      node.makeDynamic(propertyContainer, childrenContainer)
      return node
    } catch (e: UnexpectedDataTypeError) {
      throw MalformedComponentError(originalId, jsonPath, e.message)
    }
  }

  /**
   * Creates a ServerDrivenNode from a Map.
   * @param map the map to deserialize into a RenderNode.
   * @param idManager the idManager to use for generating ids for components without ids.
   * @return the resulting RenderNode.
   * @throws MalformedComponentError when a component node contains unexpected data.
   */
  @Throws(MalformedComponentError::class)
  fun buildFromJsonNode(
    jsonNode: RawJsonMap,
    id: String?,
    states: List<ServerDrivenState>?,
    scope: ViewScope,
  ): RootNode {
    val root = RootNode(id ?: scope.getIdManager().next(), states)
    updateRootNodeWithNewJsonNode(root, jsonNode, scope)
    return root
  }

  @Throws(MalformedComponentError::class)
  fun updateRootNodeWithNewJsonNode(root: RootNode, jsonNode: RawJsonMap, scope: ViewScope) {
    val childrenContainer = NodeContainer(listOf(buildNode(jsonNode, root, "$", scope)))
    root.makeDynamic(null, childrenContainer)
  }
}
