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

package br.com.zup.nimbus.core.integration.sendRequest

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*

const val BASE_URL = "http://tests.com"

fun buildScreen(sendRequestUrl: String?, shouldHaveOnErrorAndOnFinish: Boolean = true): String {
  return """{
    "_:component": "layout:container",
    "children": [
      {
        "_:component": "material:button",
        "id": "send-request-btn",
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
              }]${if (shouldHaveOnErrorAndOnFinish) """,
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

fun createPostScreen(data: String, log: String) = """{
  "_:component": "material:button",
  "id": "send-request-btn",
  "properties": {
    "onPress": [
      {
        "_:action": "sendRequest",
        "properties": {
          "method": "POST",
          "url": "$BASE_URL/post-data",
          "data": $data,
          "onSuccess": [
            {
              "_:action": "log",
              "properties": {
                "message": "$log"
              }
            }
          ],
          "onError": [
            {
              "_:action": "log",
              "properties": {
                "message": "ERROR"
              }
            }
          ]
        }
      }
    ]
  }
}"""

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
    "$BASE_URL/post-data" -> respond(
      content = ByteReadChannel(request.body.toByteArray()),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    else -> respond(
      content = ByteReadChannel(""),
      status = HttpStatusCode.NotFound,
    )
  }
}
