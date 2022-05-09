package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.*
import com.zup.nimbus.core.utils.setMapValue

private val statePathRegex = """^(\w+)((?:\.\w+)*)${'$'}""".toRegex()

class Renderer(
  private val view: ServerDrivenView,
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
    if (value is String && containsExpression(value)) return resolveExpressions(value, stateHierarchy)
    return value
  }

  /**
   * If this node is a structural component, it unfolds it into actual UI components. Otherwise, it resolves every
   * expression and deserializes every action into functions.
   *
   * Attention: this operation is not recursive on the node.
   *
   * @param node the node to resolve.
   */
  private fun resolve(node: RenderNode) {
    val componentBuilder = structuralComponents[node.component]
    if (componentBuilder != null) {
      // todo: not sure exactly how this is going to work for loops, mainly because it would need a parent.
      componentBuilder(node)
    } else {
      node.properties = node.rawProperties?.mapValues {
        resolveProperty(it.value, it.key, node, emptyList())
      }
    }
  }

  /**
   * Recursively processes the tree, resolving every expression, action or structural component.
   *
   * This recreates the state hierarchy and must be called whenever the tree structure changes. If the tree structure
   * didn't change, call "processTree" instead (faster).
   *
   *
   * @param node the tree to process.
   * @param stateHierarchy the stateHierarchy calculated until now. Initialize this with the states declared outside
   * the tree (e.g. global context). This list must be ordered from the state with the highest priority to the lowest.
   */
  private fun processTreeAndStateHierarchy(node: RenderNode, stateHierarchy: List<ServerDrivenState>) {
    node.stateHierarchy = if(node.state == null) stateHierarchy else listOf(node.state, *stateHierarchy.toTypedArray())
    resolve(node)
    node.children?.forEach { processTreeAndStateHierarchy(it, node.stateHierarchy!!) }
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
    node.children?.forEach { processTree(it) }
  }

  /**
   * Changes the value of the state passed as parameter. This doesn't reprocess the tree or trigger any re-render event,
   * it just alters the state value according to a path.
   *
   * @param state the state to alter.
   * @param path the path within the state to modify. Example: "" to alter the entire state. "foo.bar" to alter the
   * property "bar" of "foo" in the map "state.value". If "path" is not empty and "state.value" is not a mutable map, it
   * is converted to one. The path must only contain letters, numbers and underscores separated by dots.
   * @param value the new value of "state.value.$path".
   */
  private fun changeStateValue(state: ServerDrivenState, path: String, value: Any) {
    if (path.isEmpty()) {
      state.value = value
    } else {
        if (state.value !is MutableMap<*, *>) state.value = HashMap<String, Any>()
        setMapValue(state.value as MutableMap<*, *>, path, value)
    }
  }

  /**
   * Replaces the current tree with a new one. This processes the tree but doesn't trigger a re-render.
   *
   * @param tree the new tree to render.
   */
  private fun replaceEntireTree(tree: RenderNode) {
    replaceCurrentTree(tree)
    // fixme: emptyList() should be replaced with states of global-like behavior, ex.: globalState, navigationState.
    processTreeAndStateHierarchy(tree, emptyList())
  }

  /**
   * Updates a branch of the current tree with new content. This update can be either a full replacement of the branch
   * or an update to its children. This processes only the new branch, leaving the rest of the tree intact. It doesn't
   * trigger a re-render.
   *
   * @param newBranch the new branch to add to the tree.
   * @param anchor the id of the node to replace if the mode is "ReplaceItself" or the id of the node to receive the
   * new child otherwise.
   * @param mode dictates how to insert the newBranch into the current tree.
   */
  private fun updateBranch(newBranch: RenderNode, anchor: String, mode: TreeUpdateMode) {
    val currentTree = getCurrentTree() ?: throw EmptyViewError()
    val parent = currentTree.update(newBranch, anchor, mode) ?: throw AnchorNotFoundError(anchor)
    processTreeAndStateHierarchy(newBranch, parent.stateHierarchy ?: throw InvalidTreeError())
  }

  /**
   * Updates the current tree with new content and triggers a re-render event.
   *
   * @param tree the updates to the current tree. Can be either a small update or a full tree replacement.
   * @param anchor the id of the node to replace if the mode is "ReplaceItself" or the id of the node to receive the
   * new child otherwise. Defaults to the id of the root node.
   * @param mode dictates how to insert "tree" into the current tree. Defaults to "ReplaceItself".
   */
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

  /**
   * Changes the current state, reprocesses every node that could've been affected and triggers a re-render event.
   *
   * If the state is not accessible from "sourceNode" no re-render will happen and the error will be logged.
   *
   * @param sourceNode the node where the setState originated from. This is important because each node can see/modify
   * a subset of all states available. Moreover, there could be states with the same name and we must know which one
   * has been declared closest to the node (shadowing).
   * @param path the state path to alter. If we want to alter the state with id "myState", for instance, the path would
   * be "myState". If we wanted to alter the property "bar" in "foo" inside "myState", the path would be
   * "myState.foo.bar".
   * @param newValue the new value to set for the state indicated by "path".
   */
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
