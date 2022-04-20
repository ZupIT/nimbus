package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.*

class Renderer(
  private val view: ServerDrivenView,
  private val getCurrentTree: () -> RawNode?,
  private val replaceCurrentTree: (tree: RawNode) -> Unit,
  private val onFinish: (tree: ServerDrivenNode) -> Unit,
) {
  private lateinit var renderedTree: RenderedNode
  private val lifecycle = view.nimbusInstance.lifecycleHookManager

  private fun processRenderedTree(tree: RenderedNode? = null, stateHierarchy: List<ServerDrivenState>? = emptyList()) {

  }

  private fun updateEntireTree(tree: RawNode) {
    replaceCurrentTree(tree)
    lifecycle.runAfterViewSnapshot(tree)
    renderedTree = RenderedNode.fromRawNode(tree)
    processRenderedTree()
    lifecycle.runBeforeRender(renderedTree)
  }

  private fun updateBranch(newBranch: RawNode, anchor: String, mode: TreeUpdateMode) {
    insertIntoTree(getCurrentTree()!!, newBranch, anchor, mode)
    lifecycle.runAfterViewSnapshot(newBranch)
    val renderedBranch = RenderedNode.fromRawNode(newBranch)
    val anchorNode = insertIntoTree(renderedTree, renderedBranch, anchor, mode)
    processRenderedTree(renderedBranch, anchorNode.stateHierarchy)
    lifecycle.runBeforeRender(renderedBranch)
  }

  fun paint(tree: RawNode, anchor: String?, mode: TreeUpdateMode) {
    lifecycle.runBeforeViewSnapshot(tree)
    val currentTree = getCurrentTree()
    val shouldReplaceEntireTree = currentTree == null ||
      (mode == TreeUpdateMode.ReplaceItself && (anchor == null || anchor == currentTree.id))

    if(shouldReplaceEntireTree) updateEntireTree(tree)
    else updateBranch(tree, anchor ?: renderedTree.id, mode)

    onFinish(renderedTree)
  }

  fun paint(tree: RawNode) {
    paint(tree, null, TreeUpdateMode.ReplaceItself)
  }

  fun paint(tree: RawNode, mode: TreeUpdateMode) {
    paint(tree, null, mode)
  }

  fun paint(tree: RawNode, anchor: String) {
    paint(tree, anchor, TreeUpdateMode.ReplaceItself)
  }
}
