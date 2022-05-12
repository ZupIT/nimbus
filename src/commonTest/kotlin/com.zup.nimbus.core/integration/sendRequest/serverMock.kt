package com.zup.nimbus.core.integration.sendRequest

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*

const val BASE_URL = "http://tests.com"

fun buildScreen(sendRequestUrl: String?, shouldHaveOnAndOnFinish: Boolean = true): String {
  return """{
    "_:component": "layout:container",
    "children": [
      {
        "_:component": "material:button",
        "properties": {
          "text": "Load",
          "onPress": [{
            "_:action": "sendRequest",
            "properties": {
              ${if (sendRequestUrl == null) "" else "\"url\": \"$sendRequestUrl\","}
              "onSuccess": [{
                "_:action": "log",
                "properties": {
                  "message": "success"
                 }
              }]${if (shouldHaveOnAndOnFinish) """,
              "onError": [{
                "_:action": "log",
                "properties": {
                  "level": "Error",
                  "message": "error"
                }
              }],
              "onFinish": [{
                "_:action": "log",
                "properties": {
                  "message": "finish"
                }
              }]
              """ else ""}
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
