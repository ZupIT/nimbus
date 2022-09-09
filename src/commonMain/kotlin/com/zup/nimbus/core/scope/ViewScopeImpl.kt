package com.zup.nimbus.core.scope

import com.zup.nimbus.core.ServerDrivenNavigator
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.expression.ExpressionParser
import com.zup.nimbus.core.ui.UILibraryManager
import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.network.HttpClient
import com.zup.nimbus.core.network.UrlBuilder
import com.zup.nimbus.core.network.ViewClient
import com.zup.nimbus.core.tree.IdManager

interface ViewScope: NimbusScope {
  val parent: NimbusScope
  fun getView(): ServerDrivenView
  fun getNavigator(): ServerDrivenNavigator
}

class ViewScopeImpl(
  override val parent: NimbusScope,
  private val view: ServerDrivenView,
  private val navigator: ServerDrivenNavigator,
): ViewScope {
  override fun getView(): ServerDrivenView = view
  override fun getNavigator(): ServerDrivenNavigator = navigator
  override fun getLogger(): Logger = parent.getLogger()
  override fun getHttpClient(): HttpClient  = parent.getHttpClient()
  override fun getViewClient(): ViewClient = parent.getViewClient()
  override fun getUrlBuilder(): UrlBuilder = parent.getUrlBuilder()
  override fun getIdManager(): IdManager = parent.getIdManager()
  override fun getPlatform(): String = parent.getPlatform()
  override fun getUILibraryManager(): UILibraryManager = parent.getUILibraryManager()
  override fun getExpressionParser(): ExpressionParser = parent.getExpressionParser()
}
