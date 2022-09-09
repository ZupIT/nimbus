package com.zup.nimbus.core

import com.zup.nimbus.core.expression.ExpressionParser
import com.zup.nimbus.core.ui.UILibraryManager
import com.zup.nimbus.core.log.DefaultLogger
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.DefaultUrlBuilder
import com.zup.nimbus.core.network.DefaultViewClient
import com.zup.nimbus.core.scope.NimbusScope
import com.zup.nimbus.core.scope.NimbusScopeImpl
import com.zup.nimbus.core.tree.DefaultIdManager

class Nimbus(config: ServerDrivenConfig) {
  val globalState = ServerDrivenState("global", null)
  val scope: NimbusScope

  init {
    val mutableScope = NimbusScopeImpl()
    mutableScope.platform = config.platform
    mutableScope.logger = config.logger ?: DefaultLogger()
    mutableScope.uiLibraryManager = UILibraryManager(config.ui)
    mutableScope.idManager = config.idManager ?: DefaultIdManager()
    mutableScope.httpClient = config.httpClient ?: DefaultHttpClient()
    mutableScope.urlBuilder = config.urlBuilder?.let { it(config.baseUrl) } ?: DefaultUrlBuilder(config.baseUrl)
    mutableScope.viewClient = config.viewClient?.let { it(mutableScope) } ?: DefaultViewClient(mutableScope)
    mutableScope.expressionParser = ExpressionParser(mutableScope)
    scope = mutableScope
  }

  /**
   * Creates a new ServerDrivenView that uses this Nimbus instance as its dependency manager.
   *
   * Check the documentation for ServerDrivenView for more details on the parameters.
   *
   * @param getNavigator a function that returns the ServerDrivenView's navigator.
   * @param description a description for the new ServerDrivenView.
   * @return the new ServerDrivenView.
   */
  fun createView(
    getNavigator: () -> ServerDrivenNavigator,
    states: List<ServerDrivenState> = emptyList(),
    description: String? = null,
  ): ServerDrivenView {
    return ServerDrivenView(states + globalState, getNavigator, description, scope)
  }
}
