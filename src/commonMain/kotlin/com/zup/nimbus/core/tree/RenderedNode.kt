package com.zup.nimbus.core.tree

data class RenderedNode(
  override val id: String,
  override val component: String,
  override val properties: MutableMap<String, Any?>?,
  override val children: List<RenderedNode>?,
  /**
   * A list with all the states accessible by this node in descending order of priority, i.e. the first element will
   * have the highest priority and the last will have the lowest.
   */
  val stateHierarchy: List<ServerDrivenState>,
): ServerDrivenNode<RenderedNode>() {
  companion object Factory {
    fun fromRawNode(tree: RawNode, parentStateHierarchy: List<ServerDrivenState>): RenderedNode {
      val children = ArrayList<RenderedNode>()
      val stateHierarchy = if(tree.state == null) parentStateHierarchy
        else listOf(tree.state, *parentStateHierarchy.toTypedArray())
      tree.children?.forEach { children.add(fromRawNode(it, stateHierarchy)) }

      return RenderedNode(
        id = tree.id,
        component = tree.component,
        properties = tree.properties,
        children = children,
        stateHierarchy = stateHierarchy,
      )
    }
  }
}
