package com.zup.nimbus.core.tree.dynamic.builder

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.tree.dynamic.container.NodeContainer
import com.zup.nimbus.core.tree.dynamic.container.PropertyContainer
import com.zup.nimbus.core.tree.dynamic.node.DynamicNode
import com.zup.nimbus.core.tree.dynamic.node.ForEachNode
import com.zup.nimbus.core.tree.dynamic.node.IfNode
import com.zup.nimbus.core.tree.dynamic.node.RootNode
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.transformJsonObjectToMap
import com.zup.nimbus.core.utils.valueOfKey
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.decodeFromString

/**
 * Builds DynamicNodes from JSON sources.
 */
class NodeBuilder(private val nimbus: Nimbus) {
  private fun buildNode(jsonNode: Map<String, Any?>, jsonPath: String): DynamicNode {
    val originalId: String? = valueOfKey(jsonNode, "id")
    try {
      val id = originalId ?: nimbus.idManager.next()
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
        "if" -> IfNode(id, states)
        "forEach" -> ForEachNode(id, states)
        else -> DynamicNode(id, component, states)
      }

      node.propertyContainer = properties?.let {
        PropertyContainer(properties, nimbus)
      }

      node.childrenContainer = children?.let {
        val childrenAsNodes = children.mapIndexed { index, item ->
          buildNode(item, "$jsonPath.children[:$index]")
        }
        NodeContainer(childrenAsNodes)
      }

      return node
    } catch (e: UnexpectedDataTypeError) {
      throw MalformedComponentError(originalId, jsonPath, e.message)
    }
  }

  /**
   * Builds a DynamicNode tree from its string representation (json).
   *
   * This method is recursive, i.e. it will transform every Json Node into its DynamicNode representation. After
   * processing every node, the tree will be encapsulated in a RootNode.
   *
   * Attention: the returned node must be initialized before rendered.
   *
   * @throws MalformedJsonError if the string is not a valid json.
   * @throws MalformedComponentError when a component node contains unexpected data.
   */
  @Throws(MalformedJsonError::class, MalformedComponentError::class)
  fun buildFromJsonString(json: String): RootNode {
    val jsonObject: JsonObject
    try {
      jsonObject = Json.decodeFromString(json)
    } catch (e: Throwable) {
      throw MalformedJsonError("The string provided is not a valid json.")
    }
    return buildFromJsonMap(transformJsonObjectToMap(jsonObject))
  }

  /**
   * Same as buildFromJsonString, but accepts the parsed Json map instead.
   *
   * @throws MalformedComponentError when a component node contains unexpected data.
   */
  @Throws(MalformedComponentError::class)
  fun buildFromJsonMap(jsonMap: Map<String, Any?>): RootNode {
    val root = RootNode()
    root.childrenContainer = NodeContainer(listOf(buildNode(jsonMap, "$")))
    return root
  }
}
