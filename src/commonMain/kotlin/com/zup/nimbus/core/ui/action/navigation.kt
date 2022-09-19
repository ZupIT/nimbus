package com.zup.nimbus.core.ui.action

import com.zup.nimbus.core.ActionEvent
import com.zup.nimbus.core.ActionInitializedEvent
import com.zup.nimbus.core.network.ServerDrivenHttpMethod
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOfEnum
import com.zup.nimbus.core.utils.valueOfKey

private inline fun getNavigator(event: ActionEvent) = event.scope.view.navigator

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
  try {
    val request = requestFromEvent(event)
    if (isPush) getNavigator(event).push(request)
    else getNavigator(event).present(request)
  } catch (e: UnexpectedDataTypeError) {
    event.scope.nimbus.logger.error("Error while navigating.\n${e.message}")
  }
}

internal fun push(event: ActionTriggeredEvent) = pushOrPresent(event, true)

internal fun pop(event: ActionTriggeredEvent) = getNavigator(event).pop()

internal fun popTo(event: ActionTriggeredEvent) {
  try {
    getNavigator(event).popTo(valueOfKey(event.action.properties, "url"))
  } catch (e: UnexpectedDataTypeError) {
    event.scope.nimbus.logger.error("Error while navigating.\n${e.message}")
  }
}

internal fun present(event: ActionTriggeredEvent) = pushOrPresent(event, false)

internal fun dismiss(event: ActionTriggeredEvent) = getNavigator(event).dismiss()

internal fun onPushOrPresentInitialized(event: ActionInitializedEvent) {
  try {
    val prefetch: Boolean = valueOfKey(event.action.properties, "prefetch") ?: false
    if (!prefetch) return
    val request = requestFromEvent(event)
    event.scope.nimbus.viewClient.preFetch(request)
  } catch (e: Throwable) {
    event.scope.nimbus.logger.error("Error while pre-fetching view.\n${e.message}")
  }
}
