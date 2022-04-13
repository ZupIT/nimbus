package com.zup.nimbus.core

import com.zup.nimbus.core.action.ServerDrivenNavigator
import com.zup.nimbus.core.log.DefaultLogger
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.DefaultUrlBuilder
import com.zup.nimbus.core.network.DefaultViewClient
import com.zup.nimbus.core.render.ServerDrivenView
import com.zup.nimbus.core.tree.DefaultIdManager
import com.zup.nimbus.core.tree.ServerDrivenNode

class Nimbus(config: ServerDrivenConfig) {
  // From config
  val baseUrl = config.baseUrl
  val platform = config.platform
  val actions = config.actions?.toMutableMap() ?: HashMap()
  val operations = config.operations?.toMutableMap() ?: HashMap()
  val lifecycles = config.lifecycles
  val logger = config.logger ?: DefaultLogger()
  val urlBuilder = config.urlBuilder ?: DefaultUrlBuilder()
  val httpClient = config.httpClient ?: DefaultHttpClient()
  val viewClient = config.viewClient ?: DefaultViewClient()
  val idManager = config.idManager ?: DefaultIdManager()

  // Other
  val globalState = GlobalState()

  fun createView(navigator: ServerDrivenNavigator): ServerDrivenView {
    return ServerDrivenView(this, navigator)
  }

  fun createNodeFromJson(json: String): ServerDrivenNode {
    return ServerDrivenNode.fromJsonString(json, idManager)
  }

  private fun <T> addMapToMap(targetMap: Map<String, T>, newMap: Map<String, T>, mapItemName: String) {
    newMap.forEach {
      if (targetMap.containsKey(it.key)) {
        logger.warn("${mapItemName} of name \"${it.key}\" already exists and is going to be replaced. Maybe you should " +
          "consider another name.")
      }
      targetMap[it.key] = it.value
    }
  }

  fun addActions(newActions: Map<String, ActionHandler>) {
    addMapToMap(actions, newActions, "Action")
  }

  fun addOperations(newOperations: Map<String, OperationHandler>) {
    addMapToMap(operations, newOperations, "Operation")
  }
}
