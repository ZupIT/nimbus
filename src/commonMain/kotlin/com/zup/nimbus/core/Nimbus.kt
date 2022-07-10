package com.zup.nimbus.core

import com.zup.nimbus.core.action.getCoreActions
import com.zup.nimbus.core.action.getRenderHandlersForCoreActions
import com.zup.nimbus.core.component.getCoreComponents
import com.zup.nimbus.core.log.DefaultLogger
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.DefaultUrlBuilder
import com.zup.nimbus.core.network.DefaultViewClient
import com.zup.nimbus.core.operations.getDefaultOperations
import com.zup.nimbus.core.render.ServerDrivenView
import com.zup.nimbus.core.tree.DefaultIdManager
import com.zup.nimbus.core.tree.MalformedComponentError
import com.zup.nimbus.core.tree.MalformedJsonError
import com.zup.nimbus.core.tree.ObservableState
import com.zup.nimbus.core.tree.RenderNode

class Nimbus(config: ServerDrivenConfig) {
  // From config
  val baseUrl = config.baseUrl
  private val platform = config.platform
  val actions = (getCoreActions() + (config.actions ?: emptyMap())).toMutableMap()
  val actionObservers = config.actionObservers?.toMutableList() ?: ArrayList()
  val operations = (getDefaultOperations() + (config.operations?.toMutableMap() ?: emptyMap())).toMutableMap()
  val logger = config.logger ?: DefaultLogger()
  val urlBuilder = config.urlBuilder ?: DefaultUrlBuilder(baseUrl)
  val httpClient = config.httpClient ?: DefaultHttpClient()
  val idManager = config.idManager ?: DefaultIdManager()
  val viewClient = config.viewClient ?: DefaultViewClient(httpClient, urlBuilder, idManager, logger, platform)

  /**
   * Core components. These don't correspond to real UI components and never reach the UI layer. These components are
   * used to manipulate the structure of the UI tree and exists only in the core lib.
   *
   * The structural components are replaced by their result before being rendered by the UI layer.
   *
   * Examples: if (companions: then, else); switch (companions: case, default); foreach.
   */
  internal val structuralComponents = getCoreComponents()

  // Other
  val globalState = ObservableState("global", null)

  /**
   * Functions to run once an action goes through the rendering process for the first time.
   * This is currently used only for performing pre-fetches in navigation actions.
   */
  internal val onActionRendered: Map<String, ActionHandler> = getRenderHandlersForCoreActions()

  /**
   * Creates a new ServerDrivenView that uses this Nimbus instance as its dependency manager.
   *
   * Check the documentation for ServerDrivenView for more details on the parameters.
   *
   * @param getNavigator a function that returns the ServerDrivenView's navigator.
   * @param description a description for the new ServerDrivenView.
   * @return the new ServerDrivenView.
   */
  fun createView(getNavigator: () -> ServerDrivenNavigator, description: String? = null): ServerDrivenView {
    return ServerDrivenView(this, getNavigator, description)
  }

  /**
   * Creates a RenderNode from a JSON string using the idManager provided in the config (or the default idManager if
   * none has been provided.
   *
   * @param json the json string to deserialize into a RenderNode.
   * @return the resulting RenderNode.
   * @throws MalformedJsonError if the string is not a valid json.
   * @throws MalformedComponentError if a component in the JSON is malformed.
   */
  @Throws(MalformedJsonError::class, MalformedComponentError::class)
  fun createNodeFromJson(json: String): RenderNode {
    return RenderNode.fromJsonString(json, idManager)
  }

  private fun <T>addAll(target: MutableMap<String, T>, source: Map<String, T>, entity: String) {
    source.forEach {
      if (target.containsKey(it.key)) {
        logger.warn("$entity of name \"${it.key}\" already exists and is going to be replaced. Maybe you should " +
          "consider another name.")
      }
      target[it.key] = it.value
    }
  }

  fun addActions(newActions: Map<String, ActionHandler>) {
    addAll(actions, newActions, "Action")
  }

  fun addActionObservers(observers: List<ActionHandler>) {
    actionObservers.addAll(observers)
  }

  fun addOperations(newOperations: Map<String, OperationHandler>) {
    addAll(operations, newOperations, "Operation")
  }
}
