package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.ServerDrivenNode

class Renderer(
    private val view: ServerDrivenView,
    private val onTakeSnapshot: (tree: ServerDrivenNode) -> Unit,
    private val onFinish: (tree: ServerDrivenNode) -> Unit,
) {
  private var counter = 1

  private fun mockCounterDynamism(current: ServerDrivenNode): ServerDrivenNode {
    var properties: Map<String, Any?>? = null
    if (current.properties != null) {
      properties = current.properties.toMutableMap().apply {
        this.entries.forEach {
          if (it.value is String && (it.value as String).contains("@{counter}")) {
            this[it.key] = (it.value as String).replace("@{counter}", "$counter")
          }
          if (it.value == "[[ACTION:INC_COUNTER]]") {
            this[it.key] = {
              counter++
              paint(view.getCurrentTree()!!)
            }
          }
        }
      }
    }

    return ServerDrivenNode(
      id = current.id,
      component = current.component,
      state = current.state,
      properties = properties,
      children = current.children?.map { mockCounterDynamism(it) },
    )
  }

  // fixme: this is just a mock
  fun paint(tree: ServerDrivenNode, anchor: String?, mode: TreeUpdateMode) {
    val beforeViewSnapshot = view.nimbusInstance.lifecycleHooks?.beforeViewSnapshot
    if (beforeViewSnapshot != null) beforeViewSnapshot(tree)
    onTakeSnapshot(tree)
    val newTree = mockCounterDynamism(tree)
    onFinish(newTree)
  }

  fun paint(tree: ServerDrivenNode) {
    paint(tree, null, TreeUpdateMode.ReplaceItself)
  }

  fun paint(tree: ServerDrivenNode, mode: TreeUpdateMode) {
    paint(tree, null, mode)
  }

  fun paint(tree: ServerDrivenNode, anchor: String) {
    paint(tree, anchor, TreeUpdateMode.ReplaceItself)
  }
}
