package com.zup.nimbus.core.action

import com.zup.nimbus.core.ActionEvent
import com.zup.nimbus.core.ActionInitializedEvent
import com.zup.nimbus.core.network.ServerDrivenHttpMethod
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOfEnum
import com.zup.nimbus.core.utils.valueOfKey

private fun requestFromEvent(event: ActionEvent): ViewRequest {
  val properties = event.action.properties
  return ViewRequest(
    url = valueOfKey(properties, "url"),
    method = valueOfEnum(properties, "method", ServerDrivenHttpMethod.Get),
    headers = valueOfKey(properties, "headers"),
    fallback = valueOfKey(properties, "fallback"),
  )
}

private fun pushOrPresent(event: ActionTriggeredEvent, isPush: Boolean) {
  val logger = event.origin.view.nimbusInstance.logger
  try {
    val request = requestFromEvent(event)
    if (isPush) event.origin.view.getNavigator().push(request)
    else event.origin.view.getNavigator().present(request)
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while navigating.\n${e.message}")
  }
}

internal fun push(event: ActionTriggeredEvent) = pushOrPresent(event, true)

internal fun pop(event: ActionTriggeredEvent) = event.origin.view.getNavigator().pop()

internal fun popTo(event: ActionTriggeredEvent) {
  val logger = event.origin.view.nimbusInstance.logger
  try {
    event.origin.view.getNavigator().popTo(valueOfKey(event.action.properties, "url"))
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while navigating.\n${e.message}")
  }
}

internal fun present(event: ActionTriggeredEvent) = pushOrPresent(event, false)

internal fun dismiss(event: ActionTriggeredEvent) = event.origin.view.getNavigator().dismiss()

internal fun onPushOrPresentRendered(event: ActionInitializedEvent) {
  try {
    val prefetch: Boolean = valueOfKey(event.action.properties, "prefetch") ?: false
    if (!prefetch) return
    val request = requestFromEvent(event)
    event.origin.view.nimbusInstance.viewClient.preFetch(request)
  } catch (e: Throwable) {
    event.origin.view.nimbusInstance.logger.error("Error while pre-fetching view.\n${e.message}")
  }
}
