package com.zup.nimbus.core

import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.network.HttpClient
import com.zup.nimbus.core.network.UrlBuilder
import com.zup.nimbus.core.network.ViewClient
import com.zup.nimbus.core.render.ActionEvent
import com.zup.nimbus.core.tree.IdManager

typealias ActionHandler = (event: ActionEvent) -> Unit
typealias OperationHandler = (arguments: List<Any>) -> Any

data class ServerDrivenConfig(
  val baseUrl: String,
  val platform: String,
  val actions: Map<String, ActionHandler>? = null,
  val operations: Map<String, OperationHandler>? = null,
  val logger: Logger? = null,
  val urlBuilder: UrlBuilder? = null,
  val httpClient: HttpClient? = null,
  val viewClient: ViewClient? = null,
  val idManager: IdManager? = null,
)
