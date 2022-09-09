package com.zup.nimbus.core

import com.zup.nimbus.core.dependencyGraph.updateDependentsOf
import com.zup.nimbus.core.scope.NimbusScope
import com.zup.nimbus.core.scope.ViewScopeImpl
import com.zup.nimbus.core.tree.MalformedComponentError
import com.zup.nimbus.core.tree.MalformedJsonError
import com.zup.nimbus.core.tree.builder.NodeBuilder
import com.zup.nimbus.core.tree.stateful.RootNode
import com.zup.nimbus.core.tree.stateful.ServerDrivenNode
import com.zup.nimbus.core.utils.parseJsonString

private typealias Listener = ((ServerDrivenNode) -> Unit)?

class ServerDrivenView(
  private val states: List<ServerDrivenState>,
  /**
   * A function to get the navigator that spawned this view.
   *
   * Attention: this is a function so we can prevent a cyclical reference between Kotlin Native and Swift. Replacing
   * this with a direct reference will cause memory leaks.
   */
  getNavigator: () -> ServerDrivenNavigator,
  /**
   * A description for this view. Suggestion: the URL used to load the content of this view or "json", if a local json
   * string was used to load it.
   */
  val description: String? = null,
  nimbusScope: NimbusScope,
) {
  /**
   * The currently rendered tree.
   */
  private var tree: RootNode? = null
  private var listener: Listener = null
  private val scope = ViewScopeImpl(
    parent = nimbusScope,
    view = this,
    navigator = getNavigator()
  )

  fun getRendered(): RootNode? {
    return tree
  }

  fun onInit(listener: Listener) {
    this.listener = listener
  }

  /**
   * Creates a RenderNode from a Json string.
   *
   * @param json the json string to deserialize into a RenderNode.
   * @param idManager the idManager to use for generating ids for components without ids.
   * @return the resulting RenderNode.
   * @throws MalformedJsonError if the string is not a valid json.
   * @throws MalformedComponentError when a component node contains unexpected data.
   */
  @Throws(MalformedJsonError::class, MalformedComponentError::class)
  fun render(json: String) {
    val jsonMap = parseJsonString(json)
    render(jsonMap)
  }

  /**
   * Creates a RenderNode from a JsonObject.
   * @param jsonObject the json object to deserialize into a RenderNode.
   * @param idManager the idManager to use for generating ids for components without ids.
   * @return the resulting RenderNode.
   * @throws MalformedComponentError when a component node contains unexpected data.
   */
  @Throws(MalformedComponentError::class)
  fun render(jsonNode: RawJsonMap) {
    if (tree == null) {
      tree = NodeBuilder.buildFromJsonNode(jsonNode, description, states, scope)
      listener?.let { it(tree!!) }
    } else {
      NodeBuilder.updateRootNodeWithNewJsonNode(tree!!, jsonNode, scope)
      updateDependentsOf(tree!!)
    }
  }
}
