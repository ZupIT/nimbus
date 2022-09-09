package com.zup.nimbus.core.scope

import com.zup.nimbus.core.expression.ExpressionParser
import com.zup.nimbus.core.ui.UILibraryManager
import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.network.HttpClient
import com.zup.nimbus.core.network.UrlBuilder
import com.zup.nimbus.core.network.ViewClient
import com.zup.nimbus.core.tree.IdManager

interface NimbusScope {
  fun getUILibraryManager(): UILibraryManager
  fun getLogger(): Logger
  fun getHttpClient(): HttpClient
  fun getViewClient(): ViewClient
  fun getUrlBuilder(): UrlBuilder
  fun getIdManager(): IdManager
  fun getPlatform(): String
  fun getExpressionParser(): ExpressionParser
}

class NimbusScopeImpl: NimbusScope {
  internal var uiLibraryManager: UILibraryManager? = null
  internal var logger: Logger? = null
  internal var httpClient: HttpClient? = null
  internal var viewClient: ViewClient? = null
  internal var urlBuilder: UrlBuilder? = null
  internal var idManager: IdManager? = null
  internal var platform: String? = null
  internal var expressionParser: ExpressionParser? = null

  private fun error(name: String): IllegalStateException {
    return IllegalStateException("NimbusScope: $name accessed before it was initialized.")
  }

  override fun getUILibraryManager(): UILibraryManager = uiLibraryManager ?: throw error("libraryManager")
  override fun getLogger(): Logger = logger ?: throw error("logger")
  override fun getHttpClient(): HttpClient  = httpClient ?: throw error("httpClient")
  override fun getViewClient(): ViewClient = viewClient ?: throw error("viewClient")
  override fun getUrlBuilder(): UrlBuilder = urlBuilder ?: throw error("urlBuilder")
  override fun getIdManager(): IdManager = idManager ?: throw error("idManager")
  override fun getPlatform(): String = platform ?: throw error("platform")
  override fun getExpressionParser(): ExpressionParser = expressionParser ?: throw error("expressionParser")
}
