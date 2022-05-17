package com.zup.nimbus.core.action

import com.zup.nimbus.core.network.FIRST_BAD_STATUS
import com.zup.nimbus.core.network.ServerDrivenHttpMethod
import com.zup.nimbus.core.network.ServerDrivenRequest
import com.zup.nimbus.core.render.ActionEvent
import com.zup.nimbus.core.utils.transformJsonElementToKotlinType
import com.zup.nimbus.core.utils.valueOf
import com.zup.nimbus.core.utils.valueOfEnum
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

fun sendRequest(event: ActionEvent) {
  val nimbus = event.view.nimbusInstance
  val properties = event.action.properties
  try {
    // deserialize parameters
    val url: String = valueOf(properties, "url")
    val method: ServerDrivenHttpMethod = valueOfEnum(properties, "method", ServerDrivenHttpMethod.Get)
    val data: Any? = valueOf(properties, "data")
    val headers: Map<String, String>? = valueOf(properties, "headers")
    val onSuccess: ((successResponse: Map<String, Any?>) -> Unit)? = valueOf(properties, "onSuccess")
    val onError: ((errorResponse: Map<String, Any?>) -> Unit)? = valueOf(properties, "onError")
    val onFinish: ((_: Any?) -> Unit)? = valueOf(properties, "onFinish")

    // create request and coroutine scope
    val request = ServerDrivenRequest(
      url = nimbus.urlBuilder.build(url),
      method = method,
      headers = headers,
      body = if (data == null) null else Json.encodeToString(data),
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
        if (response.status >= FIRST_BAD_STATUS) throw Error(statusText)
        if (onSuccess != null) onSuccess(callbackData)
      } catch (e: Throwable) {
        nimbus.logger.error("Unable to send request.\n${e.message ?: ""}")
        if (onError != null) onError(mapOf(
          "status" to 0,
          "statusText" to "Unable to send request",
          "message" to (e.message ?: "Unknown error."),
        ))
      }
      if (onFinish != null) onFinish(null)
    }
  } catch (e: Throwable) {
    nimbus.logger.error("Error while executing action \"sendRequest\".\n${e.message ?: ""}")
  }
}
