/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.nimbus.core.ui.action

import br.com.zup.nimbus.core.ActionEvent
import br.com.zup.nimbus.core.ActionInitializedEvent
import br.com.zup.nimbus.core.network.ServerDrivenHttpMethod
import br.com.zup.nimbus.core.network.ViewRequest
import br.com.zup.nimbus.core.ActionTriggeredEvent
import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.ui.action.error.ActionExecutionError
import br.com.zup.nimbus.core.ui.action.error.ActionDeserializationError

private inline fun getNavigator(event: ActionEvent) = event.scope.view.navigator

private fun requestFromEvent(event: ActionEvent, isPushOrPresent: Boolean): ViewRequest {
  val properties = AnyServerDrivenData(event.action.properties)
  val url = properties.get("url").asString()
  val method = properties.get("method").asEnumOrNull(ServerDrivenHttpMethod.values()) ?: ServerDrivenHttpMethod.Get
  val headers = properties.get("headers").asMapOrNull()?.mapValues { it.value.asString() }
  val body = attemptJsonSerialization(properties.get("body"), event)
  val fallback = properties.get("fallback").asMapOrNull()?.mapValues { it.value.asAnyOrNull() }
  val state = if (isPushOrPresent) properties.get("state").asMapOrNull()?.mapValues { it.value.asAnyOrNull() }
  else null
  val events = if (isPushOrPresent) properties.get("events").asMapOrNull()?.map { it.value.asEvent() }
  else null
  if (properties.hasError()) throw ActionDeserializationError(event, properties)
  return ViewRequest(url, method, headers, body, fallback, state, events)
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
