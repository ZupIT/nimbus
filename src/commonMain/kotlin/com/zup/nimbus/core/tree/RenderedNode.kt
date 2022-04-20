package com.zup.nimbus.core.tree

data class RenderedNode(
  override val id: String,
  override val component: String,
  override val properties: MutableMap<String, Any?>?,
  override val children: MutableList<RenderedNode>?,
  val stateHierarchy: MutableList<ServerDrivenState>,
): ServerDrivenNode {
  companion object Factory {
    fun fromRawNode(tree: RawNode): RenderedNode {
      val children = ArrayList<RenderedNode>()
      tree.children?.forEach { children.add(fromRawNode(it)) }
      return RenderedNode(
        id = tree.id,
        component = tree.component,
        properties = tree.properties,
        children = children,
        stateHierarchy = ArrayList(),
      )
    }
  }
}
