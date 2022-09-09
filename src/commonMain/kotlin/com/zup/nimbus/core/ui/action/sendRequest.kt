package com.zup.nimbus.core.ui.action

import com.zup.nimbus.core.network.FIRST_BAD_STATUS
import com.zup.nimbus.core.network.ServerDrivenHttpMethod
import com.zup.nimbus.core.network.ServerDrivenRequest
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.tree.stateful.ServerDrivenEvent
import com.zup.nimbus.core.utils.transformJsonElementToKotlinType
import com.zup.nimbus.core.utils.valueOfEnum
import com.zup.nimbus.core.utils.valueOfKey
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

internal fun sendRequest(event: ActionTriggeredEvent) {
  val properties = event.action.properties
  try {
    // deserialize parameters
    val url: String = valueOfKey(properties, "url")
    val method: ServerDrivenHttpMethod = valueOfEnum(properties, "method", ServerDrivenHttpMethod.Get)
    val data: Any? = valueOfKey(properties, "data")
    val headers: Map<String, String>? = valueOfKey(properties, "headers")
    val onSuccess: ServerDrivenEvent? = valueOfKey(properties, "onSuccess")
    val onError: ServerDrivenEvent? = valueOfKey(properties, "onError")
    val onFinish: ServerDrivenEvent? = valueOfKey(properties, "onFinish")

    // create request and coroutine scope
    val request = ServerDrivenRequest(
      url = event.scope.getUrlBuilder().build(url),
      method = method,
      headers = headers,
      body = if (data == null) null else Json.encodeToString(data),
    )
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    // launch the request thread
    coroutineScope.launch {
      try {
        val response = event.scope.getHttpClient().sendRequest(request)
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
        event.scope.getLogger().error("Unable to send request.\n${e.message ?: ""}")
        onError?.run(mapOf(
          "status" to 0,
          "statusText" to "Unable to send request",
          "message" to (e.message ?: "Unknown error."),
        ))
      }
      onFinish?.run()
    }
  } catch (e: Throwable) {
    event.scope.getLogger().error("Error while executing action \"sendRequest\".\n${e.message ?: ""}")
  }
}
