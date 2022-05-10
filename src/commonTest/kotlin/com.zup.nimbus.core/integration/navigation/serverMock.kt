package com.zup.nimbus.core.integration.navigation

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
      "properties": {
        "text": "Previous",
        "onPress": [{
          "_:action": "pop"
        }]
      }
    }
  ]
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
    else -> respond(
      content = ByteReadChannel(""),
      status = HttpStatusCode.NotFound,
    )
  }
}
