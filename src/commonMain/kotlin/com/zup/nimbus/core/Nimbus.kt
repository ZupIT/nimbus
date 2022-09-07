package com.zup.nimbus.core

import com.zup.nimbus.core.action.getCoreActions
import com.zup.nimbus.core.action.getInitHandlersForCoreActions
import com.zup.nimbus.core.ast.ExpressionParser
import com.zup.nimbus.core.log.DefaultLogger
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.DefaultUrlBuilder
import com.zup.nimbus.core.network.DefaultViewClient
import com.zup.nimbus.core.operations.getDefaultOperations
import com.zup.nimbus.core.tree.DefaultIdManager
import com.zup.nimbus.core.tree.MalformedComponentError
import com.zup.nimbus.core.tree.MalformedJsonError
import com.zup.nimbus.core.tree.builder.ActionBuilder
import com.zup.nimbus.core.tree.builder.EventBuilder
import com.zup.nimbus.core.tree.builder.NodeBuilder

class Nimbus(config: ServerDrivenConfig) {
  // From config
  val baseUrl = config.baseUrl
  private val platform = config.platform
  private val actions = (getCoreActions() + (config.actions ?: emptyMap())).toMutableMap()
  private val actionObservers = config.actionObservers?.toMutableList() ?: ArrayList()
  val operations = (getDefaultOperations() + (config.operations?.toMutableMap() ?: emptyMap())).toMutableMap()
  val logger = config.logger ?: DefaultLogger()
  val urlBuilder = config.urlBuilder ?: DefaultUrlBuilder(baseUrl)
  val httpClient = config.httpClient ?: DefaultHttpClient()
  private val idManager = config.idManager ?: DefaultIdManager()
  val viewClient = config.viewClient ?: DefaultViewClient(httpClient, urlBuilder, logger, platform)

  // Other
  val globalState = ServerDrivenState("global", null)
  private val expressionParser = ExpressionParser(logger, operations)
  private val actionBuilder = ActionBuilder(
    actionHandlers = actions,
    actionInitHandlers = getInitHandlersForCoreActions(),
    actionObservers = actionObservers,
    expressionParser = expressionParser,
    logger = logger,
  )
  private val nodeBuilder = NodeBuilder(idManager, expressionParser, actionBuilder)

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
    return ServerDrivenView(this, getNavigator, description, nodeBuilder)
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
