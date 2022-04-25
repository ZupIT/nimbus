package com.zup.nimbus.core.tree

abstract class ServerDrivenNode<ConcreteType: ServerDrivenNode<ConcreteType>> {
  abstract val id: String
  abstract val component: String
  abstract val rawProperties: MutableMap<String, Any?>?
  abstract var properties: MutableMap<String, Any?>?
  abstract val state: ServerDrivenState?
  abstract var stateHierarchy: List<ServerDrivenState>
  abstract val children: List<ConcreteType>?

  private fun requireInsertionMode(mode: TreeUpdateMode) {
    require(mode in listOf(TreeUpdateMode.Prepend, TreeUpdateMode.Append, TreeUpdateMode.Replace)) {
      "Update mode must be Append, Prepend or Replace"
    }
  }

  fun replace(node: ConcreteType, anchor: String) {
    throw Error("Not Implemented yet!")
  }

  fun replaceChild(idOfNodeToReplace: String, newNode: ConcreteType) {
    throw Error("Not Implemented yet!")
  }

  fun insert(node: ConcreteType, anchor: String, mode: TreeUpdateMode) {
    requireInsertionMode(mode)
    throw Error("Not Implemented yet!")
  }

  fun insert(node: ConcreteType, mode: TreeUpdateMode) {
    requireInsertionMode(mode)
    throw Error("Not Implemented yet!")
  }

  fun findById(id: String): ConcreteType? {
    throw Error("Not Implemented yet!")
  }

  fun findParentById(id: String) : ConcreteType? {
    throw Error("Not Implemented yet!")
  }
}
