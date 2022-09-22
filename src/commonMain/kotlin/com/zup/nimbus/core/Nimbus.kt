package com.zup.nimbus.core

import com.zup.nimbus.core.expression.parser.ExpressionParser
import com.zup.nimbus.core.ui.UILibraryManager
import com.zup.nimbus.core.log.DefaultLogger
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.DefaultUrlBuilder
import com.zup.nimbus.core.network.DefaultViewClient
import com.zup.nimbus.core.scope.CommonScope
import com.zup.nimbus.core.tree.DefaultIdManager
import com.zup.nimbus.core.tree.dynamic.builder.EventBuilder
import com.zup.nimbus.core.tree.dynamic.builder.NodeBuilder
import com.zup.nimbus.core.ui.coreUILibrary

/**
 * The root scope of a nimbus application. Contains important objects like the logger and the httpClient.
 */
class Nimbus(config: ServerDrivenConfig): CommonScope(
  parent = null,
  states = (config.states ?: emptyList()) + ServerDrivenState("global", null),
) {
  /**
   * Manages UI elements like actions, components and operations.
   */
  val uiLibraryManager = UILibraryManager(config.coreUILibrary ?: coreUILibrary, config.ui)
  /**
   * Logger of this instance of Nimbus.
   */
  val logger = config.logger ?: DefaultLogger()
  /**
   * Responsible for making every network interaction within this nimbus instance.
   */
  val httpClient = config.httpClient ?: DefaultHttpClient()
  /**
   * Responsible for retrieving Server Driven Screens from the backend. Uses the httpClient.
   */
  val viewClient = config.viewClient?.let { it(this) } ?: DefaultViewClient(this)
  /**
   * Logic for building urls. Uses the baseUrl.
   */
  val urlBuilder = config.urlBuilder?.let { it(config.baseUrl) } ?: DefaultUrlBuilder(config.baseUrl)
  /**
   * Logic for generating unique ids for components when one hasn't been defined in the json.
   */
  val idManager = config.idManager ?: DefaultIdManager()
  /**
   * The platform currently using the Nimbus.
   */
  val platform = config.platform
  /**
   * A tool for parsing strings using the Nimbus Expression Language
   */
  val expressionParser = ExpressionParser(this)
  /**
   * Builds a node tree from a json string or map.
   */
  val nodeBuilder = NodeBuilder(this)
  /**
   * Builds an event from a json array.
   */
  val eventBuilder = EventBuilder(this)
}
