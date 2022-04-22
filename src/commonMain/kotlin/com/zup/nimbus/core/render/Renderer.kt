package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.*

class Renderer(
  private val view: ServerDrivenView,
  private val getCurrentTree: () -> RawNode?,
  private val replaceCurrentTree: (tree: RawNode) -> Unit,
  private val onFinish: (tree: ServerDrivenNode<*>) -> Unit,
) {
  /**
   * This holds the last rendered tree in memory so, in case only part of the UI changes, we don't need to calculate it
   * all over again.
   */
  private lateinit var renderedTree: RenderedNode
  private val lifecycle = view.nimbusInstance.lifecycleHookManager
  private val logger = view.nimbusInstance.logger

  private fun processRenderedTree(node: RenderedNode = renderedTree) {
    deserializeActions(node)
    resolveExpressions(node)
    node.children?.forEach { processRenderedTree(it) }
  }

  private fun replaceEntireTree(tree: RawNode) {
    replaceCurrentTree(tree)
    lifecycle.runAfterViewSnapshot(tree)
    /* fixme: below, instead of "emptyTree", we should pass a list with all states above the tree. Considering Beagle
    2.0, this would be the navigationState followed by the globalState. */
    renderedTree = RenderedNode.fromRawNode(tree, emptyList())
    processRenderedTree()
    lifecycle.runBeforeRender(renderedTree)
  }

  private fun replaceBranch(newBranch: RawNode, anchor: String) {
    val currentTree = getCurrentTree() ?: throw EmptyViewError()
    currentTree.replace(newBranch, anchor)
    lifecycle.runAfterViewSnapshot(newBranch)
    /* no need to worry about attempting to find the parent of the root here. If "anchor" referred to the root,
    "updateEntireTree" would have been called instead of this method. */
    val parent = renderedTree.findParentById(anchor) ?: throw AnchorNotFoundError(anchor)
    val renderedBranch = RenderedNode.fromRawNode(newBranch, parent.stateHierarchy)
    parent.replaceChild(anchor, renderedBranch)
    processRenderedTree(renderedBranch)
    lifecycle.runBeforeRender(renderedBranch)
  }

  private fun updateBranch(newBranch: RawNode, anchor: String, mode: TreeUpdateMode) {
    val currentTree = getCurrentTree() ?: throw EmptyViewError()
    currentTree.insert(newBranch, anchor, mode)
    lifecycle.runAfterViewSnapshot(newBranch)
    val anchorNode = renderedTree.findById(anchor) ?: throw AnchorNotFoundError(anchor)
    val renderedBranch = RenderedNode.fromRawNode(newBranch, anchorNode.stateHierarchy)
    anchorNode.insert(renderedBranch, mode)
    processRenderedTree(renderedBranch)
    lifecycle.runBeforeRender(renderedBranch)
  }

  fun paint(tree: RawNode, anchor: String?, mode: TreeUpdateMode) {
    try {
      lifecycle.runBeforeViewSnapshot(tree)
      val currentTree = getCurrentTree()
      val shouldReplaceEntireTree = currentTree == null ||
        (mode == TreeUpdateMode.ReplaceItself && (anchor == null || anchor == currentTree.id))

      when {
          shouldReplaceEntireTree -> replaceEntireTree(tree)
          mode == TreeUpdateMode.ReplaceItself -> replaceBranch(tree, anchor ?: renderedTree.id)
          else -> updateBranch(tree, anchor ?: renderedTree.id, mode)
      }

      onFinish(renderedTree)
    } catch (error: RenderingError) {
      logger.error(error.message)
    }
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
