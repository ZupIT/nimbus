package com.zup.nimbus.core.tree.node

import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.utils.valueOfKey

class ForEachNode(
  id: String,
  states: List<ServerDrivenState>?,
) : DynamicNode(id, "forEach", states, true) {
  /*private val nodeStorage = mutableMapOf<String, ServerDrivenNode>()
  private var iteratorName: String = "item"
  private var indexName: String = "index"
  private var key: String? = null
  private var hasInitialized = false

  private fun initialize(properties: Map<String, Any?>?) {
    iteratorName = valueOfKey(properties, "iteratorName") ?: iteratorName
    indexName = valueOfKey(properties, "indexName") ?: indexName
    key = valueOfKey(properties, "key")
    hasInitialized = true
  }

  private fun getIdentifierForItem(item: Any?, index: Int): String {
    val keyValue: Any? = key?.let { valueOfKey(item, it) }
    return if (keyValue == null) "$index" else "$keyValue"
  }

  private fun buildChildrenFromTemplate(
    index: Int,
    item: Any?,
    identifier: String,
    template: List<ServerDrivenNode>,
  ): List<ServerDrivenNode> {
    val itemState = ServerDrivenState(iteratorName, item)
    val indexState = ServerDrivenState(indexName, index)
    val forEachStates = listOf(itemState, indexState)
    return template.map {
      DynamicNode(
        id = "${it.id}:$identifier",
        component = it.component,
        states = forEachStates + (it.states ?: emptyList()),
        polymorphic = if (it is DynamicNode) it.polymorphic else false,
      )
    }
  }

  private fun calculateChildren(items: List<Any?>): List<ServerDrivenNode>? {
    return null
//    val template = childrenContainer?.read() ?: return null
//    return items.mapIndexed { index, item ->
//      val identifier = getIdentifierForItem(item, index)
//      nodeStorage[identifier] ?: buildChildFromTemplate(index, item, identifier, template)
//    }
  }

  override fun update() {
    val properties = propertyContainer?.read()
    if (!hasInitialized) initialize(properties)
    val items: List<Any?> = valueOfKey(properties, "items")
    val children = calculateChildren(items)
  }*/
}
