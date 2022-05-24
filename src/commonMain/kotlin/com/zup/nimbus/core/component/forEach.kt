package com.zup.nimbus.core.component

import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenState
import com.zup.nimbus.core.utils.deepCopy
import com.zup.nimbus.core.utils.valueOf

private fun getIterationKey(item: Any?, key: String?, index: Int): String {
  val keyValue: String? = if (key == null) null else valueOf(item, key)
  return keyValue ?: "$index"
}

fun forEach(node: RenderNode): List<RenderNode> {
  val items: List<Any?> = valueOf(node.properties, "items") ?: emptyList()
  val iteratorName: String = valueOf(node.properties, "iteratorName") ?: "item"
  val indexName: String = valueOf(node.properties, "indexName") ?: "index"
  val key: String? = valueOf(node.properties, "key")
  val result = ArrayList<RenderNode>()
  items.forEachIndexed { index, item ->
    node.rawChildren?.forEach { templateChild ->
      result.add(RenderNode(
        id = "${templateChild.id}:${getIterationKey(item, key, index)}",
        component = templateChild.component,
        rawProperties = deepCopy(templateChild.rawProperties) as MutableMap<String, Any?>?,
        rawChildren = deepCopy(templateChild.rawChildren) as List<RenderNode>?,
        stateId = templateChild.state?.id,
        stateValue = templateChild.state?.value,
        implicitStates = mapOf(
          iteratorName to item,
          indexName to index,
        ),
        children = null,
        properties = null,
        stateHierarchy = null,
      ))
    }
  }
  return result
}
