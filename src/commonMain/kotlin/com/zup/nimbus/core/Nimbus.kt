package com.zup.nimbus.core

import com.zup.nimbus.core.expression.parser.ExpressionParser
import com.zup.nimbus.core.ui.UILibraryManager
import com.zup.nimbus.core.log.DefaultLogger
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.DefaultUrlBuilder
import com.zup.nimbus.core.network.DefaultViewClient
import com.zup.nimbus.core.scope.CommonScope
import com.zup.nimbus.core.tree.DefaultIdManager
import com.zup.nimbus.core.tree.builder.EventBuilder
import com.zup.nimbus.core.tree.builder.NodeBuilder
import com.zup.nimbus.core.ui.coreUILibrary

class Nimbus(config: ServerDrivenConfig): CommonScope(
  parent = null,
  states = (config.states ?: emptyList()) + ServerDrivenState("global", null),
) {
  val uiLibraryManager = UILibraryManager(config.coreUILibrary ?: coreUILibrary, config.ui)
  val logger = config.logger ?: DefaultLogger()
  val httpClient = config.httpClient ?: DefaultHttpClient()
  val viewClient = config.viewClient?.let { it(this) } ?: DefaultViewClient(this)
  val urlBuilder = config.urlBuilder?.let { it(config.baseUrl) } ?: DefaultUrlBuilder(config.baseUrl)
  val idManager = config.idManager ?: DefaultIdManager()
  val platform = config.platform
  val expressionParser = ExpressionParser(this)
  val nodeBuilder = NodeBuilder(this)
  val eventBuilder = EventBuilder(this)
}
