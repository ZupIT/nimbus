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

package br.com.zup.nimbus.core.integration.navigation

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*

const val BASE_URL = "http://tests.com"

private const val SCREEN1 = """{
  "_:component": "layout:container",
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "Screen 1"
      }
    },
    {
      "_:component": "material:button",
      "id": "next",
      "properties": {
        "text": "Next",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/screen2"
          }
        }]
      }
    }
  ]
}"""

private const val SCREEN2 = """{
  "_:component": "layout:container",
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "Screen 2"
      }
    },
    {
      "_:component": "material:button",
      "id": "next",
      "properties": {
        "text": "Next",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/screen3"
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "previous",
      "properties": {
        "text": "Previous",
        "onPress": [{
          "_:action": "pop"
        }]
      }
    }
  ]
}"""

private const val SCREEN3 = """{
  "_:component": "layout:container",
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "Screen 3"
      }
    },
    {
      "_:component": "material:button",
      "id": "next",
      "properties": {
        "text": "Next (error with fallback)",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/screen4",
            "fallback": {
              "_:component": "layout:container",
              "children": [
                {
                  "_:component": "material:text",
                  "properties": {
                    "text": "Error fallback"
                  }
                },
                {
                  "_:component": "material:button",
                  "id": "previous",
                  "properties": {
                    "text": "Back to main flow",
                    "onPress": [{
                      "_:action": "pop"
                    }]
                  }
                }
              ]
            }
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "next-error",
      "properties": {
        "text": "Next (error without fallback)",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/screen4"
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "previous",
      "properties": {
        "text": "Previous",
        "onPress": [{
          "_:action": "pop"
        }]
      }
    }
  ]
}"""

const val PREFETCH1 = """{
  "_:component": "layout:container",
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "Prefetch 1"
      }
    },
    {
      "_:component": "material:button",
      "id": "go-to-prefetch2",
      "properties": {
        "text": "Go to Prefetch 2 (onPress) or Screen 2 (onLongPress)",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/prefetch2",
            "prefetch": true
          }
        }],
        "onLongPress": [{
          "_:action": "push",
          "properties": {
            "url": "/screen2",
            "prefetch": true
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "go-to-screen1-push",
      "properties": {
        "text": "Go to Screen 1 (push)",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/screen1",
            "prefetch": true,
            "onFinish": [{
              "_:action": "push",
              "properties": {
                "url": "/screen3",
                "prefetch": true
              }
            }]
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "go-to-screen1-present",
      "properties": {
        "text": "Go to Screen 1 (present)",
        "onPress": [{
          "_:action": "present",
          "properties": {
            "url": "/screen1",
            "prefetch": true
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "id": "go-to-bad-url",
      "properties": {
        "text": "Go to to bad URL",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/bad",
            "prefetch": true
          }
        }]
      }
    }
  ]
}"""

const val PREFETCH2 = """{
  "_:component": "layout:container",
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "Prefetch 2"
      }
    },
    {
      "_:component": "material:button",
      "properties": {
        "text": "Go to Prefetch 2",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/prefetch2",
            "prefetch": true
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "properties": {
        "text": "Go to Screen 1",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/screen1",
            "prefetch": true
          }
        }]
      }
    }
  ]
}"""

private const val STATEFUL_NAVIGATION_1 = """{
  "_:component": "material:button",
  "id": "next",
  "properties": {
    "text": "next",
    "onPress": [{
      "_:action": "push",
      "properties": {
        "url": "/stateful-navigation-2",
        "state": {
          "address": "Rua dos bobos, 0"
        }
      }
    }]
  }
}"""

private const val STATEFUL_NAVIGATION_2 = """{
  "_:component": "layout:text",
  "id": "address",
  "properties": {
    "text": "@{address}"
  }
}"""

val serverMock = MockEngine { request ->
  return@MockEngine when(request.url.toString()) {
    "$BASE_URL/screen1" -> respond(
      content = ByteReadChannel(SCREEN1),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    "$BASE_URL/screen2" -> respond(
      content = ByteReadChannel(SCREEN2),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    "$BASE_URL/screen3" -> respond(
      content = ByteReadChannel(SCREEN3),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    "$BASE_URL/prefetch1" -> respond(
      content = ByteReadChannel(PREFETCH1),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    "$BASE_URL/prefetch2" -> respond(
      content = ByteReadChannel(PREFETCH2),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    "$BASE_URL/stateful-navigation-1" -> respond(
      content = ByteReadChannel(STATEFUL_NAVIGATION_1),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    "$BASE_URL/stateful-navigation-2" -> respond(
      content = ByteReadChannel(STATEFUL_NAVIGATION_2),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    else -> respond(
      content = ByteReadChannel(""),
      status = HttpStatusCode.NotFound,
    )
  }
}
