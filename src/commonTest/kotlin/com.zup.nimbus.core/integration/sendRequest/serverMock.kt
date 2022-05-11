package com.zup.nimbus.core.integration.sendRequest

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*

const val BASE_URL = "http://tests.com"

fun buildScreen(sendRequestUrl: String?): String {
  return """{
    "component": "layout:container",
    "children": [
      {
        "component": "material:button",
        "properties": {
          "text": "Load",
          "onPress": [{
            "action": "sendRequest",
            "properties": {
              ${if (sendRequestUrl == null) "" else "\"url\": \"$sendRequestUrl\","}
              "onSuccess": [{
                "action": "log",
                "message": "success"
              }],
              "onError": [{
                "action": "log",
                "level": "Error",
                "message": "error"
              }],
              "onFinish": [{
                "action": "log",
                "message": "finish"
              }]
            }
          }]
        }
      }
    ]
  }"""
}

private const val SEND_REQUEST_RESPONSE = """{
  "name": "John",
  "age": 30,
  "id": "01222578545"
}"""

val serverMock = MockEngine { request ->
  return@MockEngine when(request.url.toString()) {
    "$BASE_URL/user/1" -> respond(
      content = ByteReadChannel(SEND_REQUEST_RESPONSE),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    else -> respond(
      content = ByteReadChannel(""),
      status = HttpStatusCode.NotFound,
    )
  }
}
