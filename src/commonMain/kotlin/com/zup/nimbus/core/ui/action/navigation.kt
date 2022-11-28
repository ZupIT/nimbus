package com.zup.nimbus.core.ui.action

import com.zup.nimbus.core.ActionEvent
import com.zup.nimbus.core.ActionInitializedEvent
import com.zup.nimbus.core.network.ServerDrivenHttpMethod
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import com.zup.nimbus.core.deserialization.SerializationError
import com.zup.nimbus.core.ui.action.error.ActionExecutionError
import com.zup.nimbus.core.ui.action.error.ActionDeserializationError

private inline fun getNavigator(event: ActionEvent) = event.scope.view.navigator

private fun requestFromEvent(event: ActionEvent, isPushOrPresent: Boolean): ViewRequest {
  val properties = AnyServerDrivenData(event.action.properties)
  val url = properties.get("url").asString()
  val method = properties.get("method").asEnumOrNull(ServerDrivenHttpMethod.values()) ?: ServerDrivenHttpMethod.Get
  val headers = properties.get("headers").asMapOrNull()?.mapValues { it.value.asString() }
  val body = attemptJsonSerialization(properties.get("body"), event)
  val fallback = properties.get("fallback").asMapOrNull()?.mapValues { it.value.asAnyOrNull() }
  val params = if (isPushOrPresent) properties.get("params").asMapOrNull()?.mapValues { it.value.asAnyOrNull() }
  else null
  if (properties.hasError()) throw ActionDeserializationError(event, properties)
  return ViewRequest(url, method, headers, body, fallback, params)
}

private fun pushOrPresent(event: ActionTriggeredEvent, isPush: Boolean) {
  val request = requestFromEvent(event, true)
  if (isPush) getNavigator(event).push(request)
  else getNavigator(event).present(request)
}

internal fun push(event: ActionTriggeredEvent) = pushOrPresent(event, true)

internal fun pop(event: ActionTriggeredEvent) = getNavigator(event).pop()

internal fun popTo(event: ActionTriggeredEvent) {
  val properties = AnyServerDrivenData(event.action.properties)
  val url = properties.get("url").asString()
  if (properties.hasError()) throw ActionDeserializationError(event, properties)
  getNavigator(event).popTo(url)
}

internal fun present(event: ActionTriggeredEvent) = pushOrPresent(event, false)

internal fun dismiss(event: ActionTriggeredEvent) = getNavigator(event).dismiss()

internal fun onPushOrPresentInitialized(event: ActionInitializedEvent) {
  val properties = AnyServerDrivenData(event.action.properties)
  val prefetch = properties.get("prefetch").asBooleanOrNull() ?: false
  if (!prefetch) return
  val request = requestFromEvent(event, true)
  try {
    event.scope.nimbus.viewClient.preFetch(request)
  } catch (e: Throwable) {
    throw ActionExecutionError(event, e)
  }
}
