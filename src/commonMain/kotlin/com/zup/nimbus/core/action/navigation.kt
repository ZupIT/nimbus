package com.zup.nimbus.core.action

import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.network.ServerDrivenHttpMethod
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.render.ActionTriggeredEvent
import com.zup.nimbus.core.tree.IdManager
import com.zup.nimbus.core.tree.MalformedComponentError
import com.zup.nimbus.core.tree.RenderNode

private fun getUrl(actionProperties: Map<String, *>?): String? {
  val url = actionProperties?.get("url")
  return if (url is String) url else null
}

private fun getMethod(actionProperties: Map<String, *>?, logger: Logger): ServerDrivenHttpMethod? {
  val method = actionProperties?.get("method") ?: return null
  if (method !is String) {
    logger.error("The provided method for the navigation action has invalid type. Should be String.")
  } else {
    try {
      return ServerDrivenHttpMethod.valueOf(method)
    } catch (e: IllegalArgumentException) {
      logger.error("Invalid method provided for the navigation action: $method.")
    }
  }
  return null
}

private fun getHeaders(actionProperties: Map<String, *>?, logger: Logger): Map<String, String>? {
  val headers = actionProperties?.get("headers") ?: return null
  return if (headers !is Map<*, *>) {
    logger.error("The provided value for \"headers\" has an invalid type. Should be Map<String, String>.")
    null
  } else {
    @Suppress("UNCHECKED_CAST")
    headers as Map<String, String>
  }
}

private fun getFallback(actionProperties: Map<String, *>?, idManager: IdManager, logger: Logger): RenderNode? {
  val fallback = actionProperties?.get("fallback") ?: return null
  if (fallback !is Map<*, *>) {
    logger.error("The provided value for \"fallback\" has an invalid type. Should be Map<String, Any>.")
  } else {
    try {
      @Suppress("UNCHECKED_CAST")
      return RenderNode.fromMap(fallback as Map<String, Any?>, idManager)
    } catch (e: MalformedComponentError) {
      logger.error("The component provided to \"fallback\" is Malformed.\n${e.message}")
    }
  }
  return null
}

private fun pushOrPresent(event: ActionTriggeredEvent, isPush: Boolean) {
  val logger = event.view.nimbusInstance.logger
  val properties = event.action.properties
  val url = getUrl(properties) ?: return logger.error("The pushView action requires a URL (string).")
  val request = ViewRequest(
    url = url,
    method = getMethod(properties, logger) ?: ServerDrivenHttpMethod.Get,
    headers = getHeaders(properties, logger),
    fallback = getFallback(properties, event.view.nimbusInstance.idManager, logger),
  )
  if (isPush) event.view.parentNavigator.push(request)
  else event.view.parentNavigator.present(request)
}

fun push(event: ActionTriggeredEvent) = pushOrPresent(event, true)

fun pop(event: ActionTriggeredEvent) = event.view.parentNavigator.pop()

fun popTo(event: ActionTriggeredEvent) {
  val logger = event.view.nimbusInstance.logger
  val url = getUrl(event.action.properties) ?: return logger.error("The pushView action requires a URL (string).")
  event.view.parentNavigator.popTo(url)
}

fun present(event: ActionTriggeredEvent) = pushOrPresent(event, false)

fun dismiss(event: ActionTriggeredEvent) = event.view.parentNavigator.pop()
