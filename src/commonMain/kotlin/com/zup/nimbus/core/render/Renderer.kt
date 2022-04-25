package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.*
import com.zup.nimbus.core.utils.setMapValue

private val statePathRegex = """^(\w+)((?:\.\w+)*)${'$'}""".toRegex()

class Renderer(
  view: ServerDrivenView,
  private val getCurrentTree: () -> RenderNode?,
  private val replaceCurrentTree: (tree: RenderNode) -> Unit,
  private val onFinish: () -> Unit,
) {
  private val logger = view.nimbusInstance.logger
  private val structuralComponents = view.nimbusInstance.structuralComponents

  private fun resolve(node: RenderNode) {
    val componentBuilder = structuralComponents[node.component]
    if (componentBuilder != null) {
      // todo: not sure exactly how this is going to work for loops, mainly because it would need a parent.
      componentBuilder(node)
    } else {
      deserializeActions(node)
      resolveExpressions(node)
    }
  }

  private fun processTreeAndStateHierarchy(node: RenderNode, stateHierarchy: List<ServerDrivenState>) {
    node.stateHierarchy = if(node.state == null) stateHierarchy else listOf(node.state, *stateHierarchy.toTypedArray())
    resolve(node)
    node.children?.forEach { processTreeAndStateHierarchy(it, node.stateHierarchy!!) }
  }

  private fun processTree(node: RenderNode) {
    resolve(node)
    node.children?.forEach { processTree(it) }
  }

  private fun changeStateValue(state: ServerDrivenState, path: String, value: Any) {
    if (path.isEmpty()) {
      state.value = value
    } else {
        if (state.value !is MutableMap<*, *>) state.value = HashMap<String, Any>()
        setMapValue(state.value as MutableMap<*, *>, path, value)
    }
  }

  private fun replaceEntireTree(tree: RenderNode) {
    replaceCurrentTree(tree)
    // fixme: emptyList() should be replaced with states of global-like behavior, ex.: globalState, navigationState.
    processTreeAndStateHierarchy(tree, emptyList())
  }

  private fun updateBranch(newBranch: RenderNode, anchor: String, mode: TreeUpdateMode) {
    val currentTree = getCurrentTree() ?: throw EmptyViewError()
    val parent = currentTree.update(newBranch, anchor, mode) ?: throw AnchorNotFoundError(anchor)
    processTreeAndStateHierarchy(newBranch, parent.stateHierarchy ?: throw InvalidTreeError())
  }

  fun paint(tree: RenderNode, anchor: String? = null, mode: TreeUpdateMode = TreeUpdateMode.ReplaceItself) {
    try {
      val currentTree = getCurrentTree()
      val shouldReplaceEntireTree = currentTree == null ||
        (mode == TreeUpdateMode.ReplaceItself && (anchor == null || anchor == currentTree.id))

      if (shouldReplaceEntireTree) replaceEntireTree(tree)
      else updateBranch(tree, anchor ?: currentTree!!.id, mode)

      onFinish()
    } catch (error: RenderingError) {
      logger.error(error.message)
    }
  }

  fun setState(sourceNode: RenderNode, path: String, newValue: Any) {
    try {
      val matchResult = statePathRegex.find(path) ?: throw InvalidStatePathError(path)
      val (stateId, statePath) = matchResult.destructured
      val stateHierarchy = sourceNode.stateHierarchy ?: throw InvalidTreeError()
      val state = stateHierarchy.find { it.id == stateId } ?: throw StateNotFoundError(path, sourceNode.id)
      changeStateValue(state, statePath, newValue)
      processTree(state.parent)
      onFinish()
    } catch (error: RenderingError) {
      logger.error(error.message)
    }
  }
}
