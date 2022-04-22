package com.zup.nimbus.core.tree

abstract class ServerDrivenNode<ConcreteType: ServerDrivenNode<ConcreteType>> {
  abstract val id: String
  abstract val component: String
  abstract val properties: MutableMap<String, Any?>?
  abstract val children: List<ConcreteType>?

  private fun requireInsertionMode(mode: TreeUpdateMode) {
    require(mode in listOf(TreeUpdateMode.Prepend, TreeUpdateMode.Append, TreeUpdateMode.Replace)) {
      "Update mode must be Append, Prepend or Replace"
    }
  }

  fun replace(node: ConcreteType, anchor: String) {
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
}
