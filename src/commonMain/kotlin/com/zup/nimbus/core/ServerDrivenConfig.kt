package com.zup.nimbus.core

import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.network.HttpClient
import com.zup.nimbus.core.network.UrlBuilder
import com.zup.nimbus.core.network.ViewClient
import com.zup.nimbus.core.render.LifecycleHook
import com.zup.nimbus.core.render.ServerDrivenView
import com.zup.nimbus.core.tree.ServerDrivenAction
import com.zup.nimbus.core.tree.IdManager
import com.zup.nimbus.core.tree.RawNode

typealias ActionHandler = (action: ServerDrivenAction, element: RawNode, view: ServerDrivenView) -> Unit
typealias OperationHandler = (arguments: List<Any>) -> Any


data class ServerDrivenConfig(
  val baseUrl: String,
  val platform: String,
  val actions: Map<String, ActionHandler>? = null,
  val operations: Map<String, OperationHandler>? = null,
  val lifecycleHooks: List<LifecycleHook>? = null,
  val logger: Logger? = null,
  val urlBuilder: UrlBuilder? = null,
  val httpClient: HttpClient? = null,
  val viewClient: ViewClient? = null,
  val idManager: IdManager? = null,
)
