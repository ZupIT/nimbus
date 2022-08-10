package com.zup.nimbus.core.component

import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.utils.deepCopy
import com.zup.nimbus.core.utils.valueOfKey

// todo: the foreach computes every node again every time a state in the hierarchy changes. This might cause each node
//  state to be reinitialized and also may affect performance. This first implementation won't care about this, but we
//  should look into it before releasing a stable version (1.0.0).

private fun getIterationKey(item: Any?, key: String?, index: Int): String {
  val keyValue: String? = if (key == null) null else valueOfKey(item, key)
  return keyValue ?: "$index"
}

private fun deepCopyChildren(children: List<RenderNode>?, iterationKey: String): List<RenderNode>? {
  return children?.map {
    RenderNode(
      id = "${it.id}:$iterationKey",
      component = it.component,
      rawProperties = deepCopy(it.rawProperties),
      rawChildren = deepCopyChildren(it.rawChildren, iterationKey),
      stateId = it.state?.id,
      stateValue = it.state?.value,
      implicitStates = null,
      children = null,
      properties = null,
      stateHierarchy = null,
    )
  }
}

internal fun forEachComponent(node: RenderNode): List<RenderNode> {
  val items: List<Any?> = valueOfKey(node.properties, "items") ?: emptyList()
  val iteratorName: String = valueOfKey(node.properties, "iteratorName") ?: "item"
  val indexName: String = valueOfKey(node.properties, "indexName") ?: "index"
  val key: String? = valueOfKey(node.properties, "key")
  val result = ArrayList<RenderNode>()
  items.forEachIndexed { index, item ->
    node.rawChildren?.forEach { templateChild ->
      val iterationKey = getIterationKey(item, key, index)
      result.add(RenderNode(
        id = "${templateChild.id}:$iterationKey",
        component = templateChild.component,
        rawProperties = deepCopy(templateChild.rawProperties),
        rawChildren = deepCopyChildren(templateChild.rawChildren, iterationKey),
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
