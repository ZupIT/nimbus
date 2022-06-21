package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.MalformedActionListError
import com.zup.nimbus.core.tree.RenderAction
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenState
import com.zup.nimbus.core.tree.TreeUpdateMode

private val statePathRegex = """^(\w+)((?:\.\w+)*)${'$'}""".toRegex()

class Renderer(
  private val view: ServerDrivenView,
  private val detachedStates: List<ServerDrivenState>,
  private val getCurrentTree: () -> RenderNode?,
  private val replaceCurrentTree: (tree: RenderNode) -> Unit,
  private val onFinish: () -> Unit,
) {
  private val logger = view.nimbusInstance.logger
  private val structuralComponents = view.nimbusInstance.structuralComponents

  /**
   * Resolves the property "value" of "node" recursively.
   *
   * "value" can be resolved to itself, a function (actions) or an expression result.
   *
   * @param value the property to resolve.
   * @param key the map key.
   * @param node the node "value" belongs to.
   * @param extraStates use this to declare states that should be implicit.
   * @return the resolved value.
   */
  private fun resolveProperty(
    value: Any?,
    key: String,
    node: RenderNode,
    extraStates: List<ServerDrivenState>,
  ): Any? {
    if (value is List<*>) {
      if (RenderAction.isActionList(value)) {
        try {
          return deserializeActions(
            actionList = RenderAction.createActionList(value),
            event = key,
            node = node,
            view = view,
            extraStates = extraStates,
            resolve = { propertyValue, propertyKey, implicitStates ->
              resolveProperty(propertyValue, propertyKey, node, implicitStates)
            },
          )
        } catch (error: MalformedActionListError) {
          throw RenderingError(error.message)
        }
      }
      return value.map { resolveProperty(it, key, node, extraStates) }
    }
    if (value is Map<*, *>) return value.mapValues { resolveProperty(it.value, it.key.toString(), node, extraStates) }
    val stateHierarchy = (node.stateHierarchy ?: emptyList()) + extraStates
    if (value is String && containsExpression(value)) {
      return resolveExpressions(value, stateHierarchy, view.nimbusInstance.operations, logger)
    }
    return value
  }

  /**
   * Resolves every expression and deserializes every action into functions.
   *
   * Attention: this operation is not recursive on the node.
   *
   * @param node the node to resolve.
   */
  private fun resolve(node: RenderNode) {
    node.properties = node.rawProperties?.mapValues {
      resolveProperty(it.value, it.key, node, emptyList())
    }
    node.isRendered = true
  }

  private fun computeStateHierarchy(node: RenderNode, parentHierarchy: List<ServerDrivenState>) {
    if (node.state == null && node.implicitStates?.isEmpty() != false) {
      node.stateHierarchy = parentHierarchy
    } else {
      val newHierarchy = ArrayList<ServerDrivenState>()
      if (node.state != null) newHierarchy.add(node.state)
      if (node.implicitStates != null) newHierarchy.addAll(node.implicitStates)
      newHierarchy.addAll(parentHierarchy)
      node.stateHierarchy = newHierarchy
    }
  }

  /* This needs to be recursive because a structural component can yield another structural component, without a
  wrapping component. If this was not recursive, this new structural component would never get processed. */
  private fun buildStructuralComponent(
    node: RenderNode,
    builder: (node: RenderNode) -> List<RenderNode>,
    stateHierarchy: List<ServerDrivenState>,
  ): List<RenderNode> {
    val children = ArrayList<RenderNode>()
    computeStateHierarchy(node, stateHierarchy)
    resolve(node)
    val result = builder(node)
    result.forEach {
      val componentBuilder = structuralComponents[it.component]
      if (componentBuilder == null) {
        processTreeAndStateHierarchy(it, node.stateHierarchy!!)
        children.add(it)
      } else {
        children.addAll(buildStructuralComponent(it, componentBuilder, node.stateHierarchy!!))
      }
    }
    return children
  }

  private fun unfoldStructuralComponents(node: RenderNode) {
    val children = ArrayList<RenderNode>()
    node.rawChildren?.forEach { child ->
      val componentBuilder = structuralComponents[child.component]
      if (componentBuilder == null) children.add(child)
      else children.addAll(buildStructuralComponent(child, componentBuilder, node.stateHierarchy!!))
    }
    node.children = children
  }

  /**
   * Creates the `node.children` array from the `node.rawChildren`. In most cases, `children` will just be a pointer to
   * `rawChildren`, but when the node contains a structural node that needs to be unfolded, its `children` array will be
   * a version of its `rawChildren` array where every structural node is replaced by its result.
   *
   * This also processes every child of `node` according to `shouldProcessStateHierarchy`. Since structural components
   * always represent a new structure, they will always be processed with `processTreeAndStateHierarchy` despite the
   * value of `shouldProcessStateHierarchy`.
   *
   * At the moment of writing, there are two structural components: `if` and `foreach`:
   *
   * If the `rawChildren` contains something like:
   * - If
   *   - Then
   *     - NodeA
   *   - Else
   *     - NodeB
   *
   * The children will contain only:
   * - NodeA or;
   * - NodeB, depending on the result of `structuralComponents["if"](node)`
   *
   * If the `rawChildren` contains something like:
   * - Foreach
   *   - Template
   *
   * The children will contain something like:
   * - ComponentResultingFromIteration1
   * - ComponentResultingFromIteration2
   * - ComponentResultingFromIteration3
   * - and more (or less) depending on the result of `structuralComponents["foreach"](node)`
   *
   * @param node the node to have its children processed from its rawChildren.
   * @param shouldProcessStateHierarchy whether to process the children with `processTreeAndStateHierarchy` (true) or
   * `processTree` (false). Makes no difference for structural children, they will always use
   * `processTreeAndStateHierarchy`.
   */
  private fun createChildrenFromRawChildren(node: RenderNode, shouldProcessStateHierarchy: Boolean) {
    var hasStructuralNode = false

    node.rawChildren?.forEach {
      val isStructuralNode = structuralComponents.containsKey(it.component)
      if (isStructuralNode) {
        hasStructuralNode = true
        return@forEach
      }
      if (shouldProcessStateHierarchy) {
        processTreeAndStateHierarchy(it, node.stateHierarchy ?: throw NoStateHierarchyError())
      } else {
        processTree(it)
      }
    }

    if (hasStructuralNode) unfoldStructuralComponents(node)
    else node.children = node.rawChildren
  }

  /**
   * Recursively processes the tree, resolving every expression, action or structural component.
   *
   * This recreates the state hierarchy and must be called whenever the tree structure changes. If the tree structure
   * didn't change, call "processTree" instead (faster).
   *
   * @param node the tree to process.
   * @param stateHierarchy the stateHierarchy calculated until now. Initialize this with the states declared outside
   * the tree (e.g. global context). This list must be ordered from the state with the highest priority to the lowest.
   */
  private fun processTreeAndStateHierarchy(node: RenderNode, stateHierarchy: List<ServerDrivenState>) {
    computeStateHierarchy(node, stateHierarchy)
    resolve(node)
    createChildrenFromRawChildren(node, true)
  }

  /**
   * Recursively processes the tree, resolving every expression, action or structural component.
   *
   * This will use the state hierarchy that has already been calculated instead of recalculating it. Be sure to only
   * call it if the tree structure is maintained, otherwise, call "processTreeAndStateHierarchy" instead (slower).
   *
   * @param node the tree to process.
   */
  private fun processTree(node: RenderNode) {
    resolve(node)
    createChildrenFromRawChildren(node, false)
  }

  /**
   * Replaces the current tree with a new one. This processes the tree but doesn't trigger a re-render.
   *
   * @param tree the new tree to render.
   */
  private fun replaceEntireTree(tree: RenderNode) {
    replaceCurrentTree(tree)
    processTreeAndStateHierarchy(tree, detachedStates)
  }

  /**
   * Updates a branch of the current tree with new content. This update can be either a full replacement of the branch
   * or an update to its children. This processes only the node "anchor" refers to, leaving the rest of the tree intact.
   * It doesn't trigger a re-render.
   *
   * @param newBranch the new branch to add to the tree.
   * @param anchor the id of the node to replace if the mode is "ReplaceItself" or the id of the node to receive the
   * new child otherwise.
   * @param mode dictates how to insert the newBranch into the current tree.
   */
  private fun updateBranch(newBranch: RenderNode, anchor: String, mode: TreeUpdateMode) {
    val currentTree = getCurrentTree() ?: throw EmptyViewError()
    val parent = currentTree.update(newBranch, anchor, mode) ?: throw AnchorNotFoundError(anchor)
    processTreeAndStateHierarchy(parent, parent.stateHierarchy ?: throw InvalidTreeError())
  }

  /**
   * Updates the current tree with new content and triggers a re-render event.
   *
   * @param tree the updates to the current tree. Can be either a small update or a full tree replacement.
   * @param anchor the id of the node to replace if the mode is "ReplaceItself" or the id of the node to receive the
   * new child otherwise. Defaults to the id of the root node.
   * @param mode dictates how to insert "tree" into the current tree. Defaults to "ReplaceItself".
   * @throws UnexpectedRootStructuralComponent when th root node is a structural component.
   */
  @Throws(UnexpectedRootStructuralComponent::class)
  fun paint(tree: RenderNode, anchor: String? = null, mode: TreeUpdateMode = TreeUpdateMode.ReplaceItself) {
    if (structuralComponents.containsKey(tree.component)) {
      throw UnexpectedRootStructuralComponent()
    }
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

  /**
   * Reprocesses the entire tree without changing its structure. Useful for updating values of states that live outside
   * the tree, example: the global state.
   */
  fun refresh() {
    val current = getCurrentTree() ?: return logger.error("Can't refresh blank ServerDrivenView.")
    processTree(current)
    onFinish()
  }

  /**
   * Changes the current state, reprocesses every node that could've been affected and triggers a re-render event.
   *
   * If the state is not accessible from "sourceNode" no re-render will happen and the error will be logged.
   *
   * If the node to update is detached from the tree (global state, for instance). The UI will not be updated from here
   * since it is expected that an outside listener will take care of it.
   *
   * @param sourceNode the node where the setState originated from. This is important because each node can see/modify
   * a subset of all states available. Moreover, there could be states with the same name and we must know which one
   * has been declared closest to the node (shadowing).
   * @param path the state path to alter. If we want to alter the state with id "myState", for instance, the path would
   * be "myState". If we wanted to alter the property "bar" in "foo" inside "myState", the path would be
   * "myState.foo.bar".
   * @param newValue the new value to set for the state indicated by "path".
   */
  fun setState(sourceNode: RenderNode, path: String, newValue: Any?) {
    try {
      val matchResult = statePathRegex.find(path) ?: throw InvalidStatePathError(path)
      val (stateId, statePath) = matchResult.destructured
      val stateHierarchy = sourceNode.stateHierarchy ?: throw InvalidTreeError()
      val state = stateHierarchy.find { it.id == stateId } ?: throw StateNotFoundError(path, sourceNode.id)
      state.set(newValue, statePath)
      if (state.parent != null) {
        processTree(state.parent)
        onFinish()
      }
    } catch (error: RenderingError) {
      logger.error(error.message)
    }
  }
}
