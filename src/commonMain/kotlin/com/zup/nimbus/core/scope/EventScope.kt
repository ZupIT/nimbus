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
import com.zup.nimbus.core.tree.stateful.ServerDrivenEvent
import com.zup.nimbus.core.tree.stateful.ServerDrivenNode
import com.zup.nimbus.core.tree.stateful.Stateful

interface EventScope: ViewScope {
  override val parent: ViewScope
  fun getEvent(): ServerDrivenEvent
  fun getNode(): ServerDrivenNode
}

class EventScopeImpl(
  override val parent: ViewScope,
  private val event: ServerDrivenEvent,
): EventScope {
  private fun getNode(stateful: Stateful): ServerDrivenNode {
    if (stateful is ServerDrivenNode) return stateful
    stateful.parent?.let { return getNode(it) }
    throw IllegalStateException("The event in this scope is not linked to a node.")
  }

  override fun getNode(): ServerDrivenNode = getNode(event)
  override fun getEvent(): ServerDrivenEvent = event
  override fun getView(): ServerDrivenView = parent.getView()
  override fun getNavigator(): ServerDrivenNavigator = parent.getNavigator()
  override fun getUILibraryManager(): UILibraryManager = parent.parent.getUILibraryManager()
  override fun getLogger(): Logger = parent.parent.getLogger()
  override fun getHttpClient(): HttpClient = parent.parent.getHttpClient()
  override fun getViewClient(): ViewClient = parent.parent.getViewClient()
  override fun getUrlBuilder(): UrlBuilder = parent.parent.getUrlBuilder()
  override fun getIdManager(): IdManager = parent.parent.getIdManager()
  override fun getPlatform(): String = parent.parent.getPlatform()
  override fun getExpressionParser(): ExpressionParser = parent.parent.getExpressionParser()
}
