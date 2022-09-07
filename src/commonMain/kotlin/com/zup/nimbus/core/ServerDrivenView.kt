package com.zup.nimbus.core

import com.zup.nimbus.core.dependencyGraph.updateDependentsOf
import com.zup.nimbus.core.tree.MalformedComponentError
import com.zup.nimbus.core.tree.MalformedJsonError
import com.zup.nimbus.core.tree.builder.NodeBuilder
import com.zup.nimbus.core.tree.stateful.RootNode
import com.zup.nimbus.core.tree.stateful.ServerDrivenNode
import com.zup.nimbus.core.utils.parseJsonString
import com.zup.nimbus.core.utils.transformJsonObjectToMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

private typealias Listener = ((ServerDrivenNode) -> Unit)?

class ServerDrivenView(
  /**
   * The instance of Nimbus that created this ServerDrivenView.
   */
  val nimbusInstance: Nimbus,
  /**
   * A function to get the navigator that spawned this view.
   *
   * Attention: this is a function so we can prevent a cyclical reference between Kotlin Native and Swift. Replacing
   * this with a direct reference will cause memory leaks.
   */
  val getNavigator: () -> ServerDrivenNavigator,
  /**
   * A description for this view. Suggestion: the URL used to load the content of this view or "json", if a local json
   * string was used to load it.
   */
  val description: String? = null,
  private val nodeBuilder: NodeBuilder,
) {
  /**
   * The currently rendered tree.
   */
  private var tree: RootNode? = null
  private var listener: Listener = null

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
      tree = nodeBuilder.buildFromJsonNode(jsonNode, description, listOf(nimbusInstance.globalState), this)
      listener?.let { it(tree!!) }
    } else {
      nodeBuilder.updateRootNodeWithNewJsonNode(tree!!, jsonNode, this)
      updateDependentsOf(setOf(tree!!))
    }
  }
}
