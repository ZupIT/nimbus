package com.zup.nimbus.core.ui.action

import com.zup.nimbus.core.network.FIRST_BAD_STATUS
import com.zup.nimbus.core.network.ServerDrivenHttpMethod
import com.zup.nimbus.core.network.ServerDrivenRequest
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import com.zup.nimbus.core.deserialization.SerializationError
import com.zup.nimbus.core.ui.action.error.ActionDeserializationError
import com.zup.nimbus.core.ui.action.error.ActionExecutionError
import com.zup.nimbus.core.utils.transformJsonElementToKotlinType
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

internal fun sendRequest(event: ActionTriggeredEvent) {
  val nimbus = event.scope.nimbus

  // deserialize properties
  val properties = AnyServerDrivenData(event.action.properties)
  val url = properties.get("url").asString()
  val method = properties.get("method").asEnumOrNull(ServerDrivenHttpMethod.values()) ?: ServerDrivenHttpMethod.Get
  val data = attemptJsonSerialization(properties.get("data"), event)
  val headers = properties.get("headers").asMapOrNull()?.mapValues { it.value.asString() } ?: emptyMap()
  val onSuccess = properties.get("onSuccess").asEventOrNull()
  val onError = properties.get("onError").asEventOrNull()
  val onFinish = properties.get("onFinish").asEventOrNull()

  if (properties.hasError()) throw ActionDeserializationError(event, properties)

  // create request and coroutine scope
  val request = ServerDrivenRequest(
    url = nimbus.urlBuilder.build(url),
    method = method,
    headers = if (data == null) headers else mapOf("Content-Type" to "application/json") + headers,
    body = data,
  )
  val coroutineScope = CoroutineScope(Dispatchers.Default)

  // launch the request thread
  coroutineScope.launch {
    try {
      val response = nimbus.httpClient.sendRequest(request)
      val callbackData = HashMap<String, Any?>()
      val statusText = HttpStatusCode.fromValue(response.status).description
      callbackData["status"] = response.status
      callbackData["statusText"] = statusText
      try {
        val jsonElement = Json.decodeFromString<JsonElement>(response.body)
        callbackData["data"] = transformJsonElementToKotlinType(jsonElement)
      } catch (e: Throwable) {
        callbackData["data"] = response.body
      }
      // todo: verify
      if (response.status >= FIRST_BAD_STATUS) @Suppress("TooGenericExceptionThrown") throw Error(statusText)
      onSuccess?.run(callbackData)
    } catch (e: Throwable) {
      nimbus.logger.error("Unable to send request.\n${e.message ?: ""}")
      onError?.run(mapOf(
        "status" to 0,
        "statusText" to "Unable to send request",
        "message" to (e.message ?: "Unknown error."),
      ))
    }
    onFinish?.run()
  }
}
