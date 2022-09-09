package com.zup.nimbus.core.ui.action

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
  try {
    val request = requestFromEvent(event)
    if (isPush) event.scope.getNavigator().push(request)
    else event.scope.getNavigator().present(request)
  } catch (e: UnexpectedDataTypeError) {
    event.scope.getLogger().error("Error while navigating.\n${e.message}")
  }
}

internal fun push(event: ActionTriggeredEvent) = pushOrPresent(event, true)

internal fun pop(event: ActionTriggeredEvent) = event.scope.getNavigator().pop()

internal fun popTo(event: ActionTriggeredEvent) {
  try {
    event.scope.getNavigator().popTo(valueOfKey(event.action.properties, "url"))
  } catch (e: UnexpectedDataTypeError) {
    event.scope.getLogger().error("Error while navigating.\n${e.message}")
  }
}

internal fun present(event: ActionTriggeredEvent) = pushOrPresent(event, false)

internal fun dismiss(event: ActionTriggeredEvent) = event.scope.getNavigator().dismiss()

internal fun onPushOrPresentRendered(event: ActionInitializedEvent) {
  try {
    val prefetch: Boolean = valueOfKey(event.action.properties, "prefetch") ?: false
    if (!prefetch) return
    val request = requestFromEvent(event)
    event.scope.getViewClient().preFetch(request)
  } catch (e: Throwable) {
    event.scope.getLogger().error("Error while pre-fetching view.\n${e.message}")
  }
}
