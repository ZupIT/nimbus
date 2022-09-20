package com.zup.nimbus.core.tree.dynamic.node

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.scope.StateOnlyScope
import com.zup.nimbus.core.scope.closestScopeWithType
import com.zup.nimbus.core.scope.getPathToScope
import com.zup.nimbus.core.tree.dynamic.container.NodeContainer
import com.zup.nimbus.core.utils.valueOfKey

/**
 * A rendered list that can change must somehow identify each of its item. This encapsulates an item with an object
 * that has an id and is comparable (equals). Ideally, the user will pass a key to allow us to identify the unique
 * property within an item. If this doesn't happen, we'll use the index of the item in the list.
 */
private class IdentifiableItem(val value: Any?, index: Int, key: String?) {
  val id: String

  init {
    // fixme: we probably want to change this to valueOfPath, but it would be computationally more expensive
    val keyValue: Any? = key?.let { valueOfKey(value, it) }
    id = if (keyValue == null) "$index" else "$keyValue"
  }

  override fun equals(other: Any?): Boolean {
    return other is IdentifiableItem && id == other.id
  }

  override fun hashCode(): Int {
    var result = value?.hashCode() ?: 0
    result = 31 * result + id.hashCode()
    return result
  }
}

// fixme: (1) just like the if component, we have the problem here of not freeing unused nodes. This can be interesting
//  because nodes that leave the list will be the same if they come back, preserving their state. On the other hand,
//  we're not freeing up the memory and these nodes will be deallocated only when the root node is. We need to decide if
//  this is a feature or a bug, after this, this comment can be removed.
//
// fixme: (2) when an item state is updated via setState, the list won't trigger updates to entities that depend on it.
//  A fix for this would be to make the state referred by the property "items" dependent on each item state created by
//  the forEach. This has a cost though, do we want to add this cost to every forEach? Do we want to control this
//  behavior via a property?
//
// fixme: (3) if an item of the array (items) is directly set via setState, the item will not update.
//  Example: setState(state = myDataSet, path = "[2]", value = "new value"); this won't trigger an update.
//  A possible fix would be to make each item state depend on the array. The problem is, by implementing this fix and
//  the fix to the previous issue (2), we create lots of cyclic dependencies, which will end up in an infinity loop
//  when processed by the function updateDependents.
//
// fixme: (4) when a child is moved in the dataset, the index state doesn't update.
/**
 * ForEachNode is a polymorphic DynamicNode that iterates over a data set (items) and generate some UI for each of its
 * items. The template used for each iteration is the children in the original json.
 *
 * Reminder: a polymorphic node is a special type of dynamic node that is always skipped by the NodeContainer when
 * calculating the children of a node. Only the non-polymorphic children of a polymorphic node ends up in the UI tree.
 * To know more about polymorphic nodes, read the documentation for "polymorphic" in "DynamicNode".
 *
 * A ForEach node in its json form is represented by the following type definition (Typescript):
 * interface ForEach {
 *   items?: any[], // the data set to iterate over
 *   iteratorName?: string, // the state id to use for the current item; default: item
 *   indexName?: string, // the state id to use for the current index; default: index
 *   key?: string, // a property key to identify each item in the data set
 *   children: Component[], // the template
 * }
 */
class ForEachNode(
  id: String,
  states: List<ServerDrivenState>?,
) : DynamicNode(id, "forEach", states, true) {
  /**
   * We can't recreate the entire subtree everytime an item is added or removed. For this reason, we save every node
   * upon its creation and just recover it when updating the content of the ForEach.
   */
  private val nodeStorage = mutableMapOf<String, NodeContainer>()
  private var iteratorName: String = "item"
  private var indexName: String = "index"
  private var key: String? = null
  private var hasInitialized = false
  private var items: List<IdentifiableItem> = emptyList()
  private val nimbus: Nimbus? by lazy { closestScopeWithType() }

  override fun initialize(scope: Scope) {
    parent = scope
    propertyContainer?.initialize(this)
    propertyContainer?.addDependent(this)
    properties = propertyContainer?.read()
    iteratorName = valueOfKey(properties, "iteratorName") ?: iteratorName
    indexName = valueOfKey(properties, "indexName") ?: indexName
    key = valueOfKey(properties, "key")
    hasInitialized = true
    update()
    hasChanged = false
  }

  private fun buildChild(
      index: Int,
      item: IdentifiableItem,
      template: NodeContainer,
  ): NodeContainer {
    val itemState = ServerDrivenState(iteratorName, item.value)
    val indexState = ServerDrivenState(indexName, index)
    val itemScope = StateOnlyScope(this, listOf(itemState, indexState))
    val child = template.clone(":${item.id}")
    nodeStorage[item.id] = child
    child.initialize(itemScope)
    child.addDependent(this)
    return child
  }

  private fun calculateChildren(): List<DynamicNode>? {
    return childrenContainer?.let { childrenContainer ->
      val containers = items.mapIndexed { index, item ->
        nodeStorage[item.id] ?: buildChild(index, item, childrenContainer)
      }
      containers.map { it.read() }.flatten()
    }
  }

  private fun warnIfUpdatingWithoutKey() {
    if (items.isNotEmpty() && key?.isEmpty() != false) {
      nimbus?.logger?.warn("You're trying to modify a forEach component after its initialization without providing " +
        "a key for its elements. This can cause undesirable behavior and performance issues. Please, provide " +
        "the property \"key\" for the forEach component at:\n${this.getPathToScope()}.")
    }
  }

  override fun update() {
    properties = propertyContainer?.read()
    val newItems: List<Any?> = valueOfKey(properties, "items") ?: emptyList()
    val newIdentified = newItems.mapIndexed { index, item -> IdentifiableItem(item, index, key) }
    if (newIdentified != items) {
      warnIfUpdatingWithoutKey()
      items = newIdentified
      val newChildren = calculateChildren()
      hasChanged = true
      children = newChildren
    }
  }

  override fun clone(idSuffix: String): DynamicNode = clone(idSuffix) { id, states -> ForEachNode(id, states) }
}
