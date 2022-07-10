package com.zup.nimbus.core.action

import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.network.ServerDrivenHttpMethod
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.render.ActionEvent
import com.zup.nimbus.core.tree.IdManager
import com.zup.nimbus.core.tree.MalformedComponentError
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOf
import com.zup.nimbus.core.utils.valueOfEnum

private fun getFallback(actionProperties: Map<String, *>?, idManager: IdManager, logger: Logger): RenderNode? {
  val fallback: Map<String, Any?> = valueOf(actionProperties, "fallback") ?: return null
  return try {
    RenderNode.fromMap(fallback, idManager)
  } catch (e: MalformedComponentError) {
    logger.error("The component provided to \"fallback\" is Malformed.\n${e.message}")
    null
  }
}

private fun requestFromEvent(event: ActionEvent): ViewRequest {
  val logger = event.view.nimbusInstance.logger
  val properties = event.action.properties
  return ViewRequest(
    url = valueOf(properties, "url"),
    method = valueOfEnum(properties, "method", ServerDrivenHttpMethod.Get),
    headers = valueOf(properties, "headers"),
    fallback = getFallback(properties, event.view.nimbusInstance.idManager, logger),
  )
}

private fun pushOrPresent(event: ActionEvent, isPush: Boolean) {
  val logger = event.view.nimbusInstance.logger
  try {
    val request = requestFromEvent(event)
    if (isPush) event.view.getNavigator().push(request)
    else event.view.getNavigator().present(request)
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while navigating.\n${e.message}")
  }
}

internal fun push(event: ActionEvent) = pushOrPresent(event, true)

internal fun pop(event: ActionEvent) = event.view.getNavigator().pop()

internal fun popTo(event: ActionEvent) {
  val logger = event.view.nimbusInstance.logger
  try {
    event.view.getNavigator().popTo(valueOf(event.action.properties, "url"))
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while navigating.\n${e.message}")
  }
}

internal fun present(event: ActionEvent) = pushOrPresent(event, false)

internal fun dismiss(event: ActionEvent) = event.view.getNavigator().dismiss()

internal fun onPushOrPresentRendered(event: ActionEvent) {
  try {
    val prefetch: Boolean = valueOf(event.action.properties, "prefetch") ?: false
    if (!prefetch) return
    val request = requestFromEvent(event)
    event.view.nimbusInstance.viewClient.preFetch(request)
  } catch (e: Throwable) {
    event.view.nimbusInstance.logger.error("Error while pre-fetching view.\n${e.message}")
  }
}
