package com.zup.nimbus.core.action

import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.network.ServerDrivenHttpMethod
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.render.ActionTriggeredEvent
import com.zup.nimbus.core.tree.IdManager
import com.zup.nimbus.core.tree.MalformedComponentError
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOf
import com.zup.nimbus.core.utils.valueOfEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private fun getFallback(actionProperties: Map<String, *>?, idManager: IdManager, logger: Logger): RenderNode? {
  val fallback: Map<String, Any?> = valueOf(actionProperties, "fallback") ?: return null
  return try {
    RenderNode.fromMap(fallback, idManager)
  } catch (e: MalformedComponentError) {
    logger.error("The component provided to \"fallback\" is Malformed.\n${e.message}")
    null
  }
}

private fun requestFromEvent(event: ActionTriggeredEvent): ViewRequest {
  val logger = event.view.nimbusInstance.logger
  val properties = event.action.properties
  return ViewRequest(
    url = valueOf(properties, "url"),
    method = valueOfEnum(properties, "method", ServerDrivenHttpMethod.Get),
    headers = valueOf(properties, "headers"),
    fallback = getFallback(properties, event.view.nimbusInstance.idManager, logger),
  )
}

private fun pushOrPresent(event: ActionTriggeredEvent, isPush: Boolean) {
  val logger = event.view.nimbusInstance.logger
  try {
    val request = requestFromEvent(event)
    if (isPush) event.view.parentNavigator.push(request)
    else event.view.parentNavigator.present(request)
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while navigating.\n${e.message}")
  }
}

fun push(event: ActionTriggeredEvent) = pushOrPresent(event, true)

fun pop(event: ActionTriggeredEvent) = event.view.parentNavigator.pop()

fun popTo(event: ActionTriggeredEvent) {
  val logger = event.view.nimbusInstance.logger
  try {
    event.view.parentNavigator.popTo(valueOf(event.action.properties, "url"))
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while navigating.\n${e.message}")
  }
}

fun present(event: ActionTriggeredEvent) = pushOrPresent(event, false)

fun dismiss(event: ActionTriggeredEvent) = event.view.parentNavigator.pop()

fun onPushOrPresentRendered(event: ActionTriggeredEvent) {
  val prefetch: Boolean = valueOf(event.action.properties, "prefetch") ?: false
  if (!prefetch) return
  try {
    val request = requestFromEvent(event)
    event.view.nimbusInstance.viewClient.preFetch(request)
  } catch (e: Throwable) {
    event.view.nimbusInstance.logger.error("Error while pre-fetching view.\n${e.message}")
  }
}
