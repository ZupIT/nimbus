package com.zup.nimbus.core.integration.navigation

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*

const val BASE_URL = "http://tests.com"

private val screen1 = """{
  "component": "layout:container",
  "children": [
    {
      "component": "material:text",
      "properties": {
        "text": "Screen 1"
      }
    },
    {
      "component": "material:button",
      "properties": {
        "text": "Next",
        "onPress": [{
          "action": "pushView",
          "properties": {
            "url": "/screen2"
          }
        }]
      }
    }
  ]
}"""

private val screen2 = """{
  "component": "layout:container",
  "children": [
    {
      "component": "material:text",
      "properties": {
        "text": "Screen 2"
      }
    },
    {
      "component": "material:button",
      "properties": {
        "text": "Next",
        "onPress": [{
          "action": "pushView",
          "properties": {
            "url": "/screen3"
          }
        }]
      }
    },
    {
      "component": "material:button",
      "properties": {
        "text": "Previous",
        "onPress": [{
          "action": "popView"
        }]
      }
    }
  ]
}"""

private val screen3 = """{
  "component": "layout:container",
  "children": [
    {
      "component": "material:text",
      "properties": {
        "text": "Screen 3"
      }
    },
    {
      "component": "material:button",
      "properties": {
        "text": "Next (error with fallback)",
        "onPress": [{
          "action": "pushView",
          "properties": {
            "url": "/screen4",
            "fallback": {
              "component": "layout:container",
              "children": [
                {
                  "component": "material:text",
                  "properties": {
                    "text": "Error fallback"
                  }
                },
                {
                  "component": "material:button",
                  "properties": {
                    "text": "Back to main flow",
                    "onPress": [{
                      "action": "popView"
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
      "component": "material:button",
      "properties": {
        "text": "Next (error without fallback)",
        "onPress": [{
          "action": "pushView",
          "properties": {
            "url": "/screen4"
          }
        }]
      }
    },
    {
      "component": "material:button",
      "properties": {
        "text": "Previous",
        "onPress": [{
          "action": "popView"
        }]
      }
    }
  ]
}"""

val serverMock = MockEngine { request ->
    return@MockEngine when(request.url.toString()) {
      "$BASE_URL/screen1" -> respond(
        content = ByteReadChannel(screen1),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
      "$BASE_URL/screen2" -> respond(
        content = ByteReadChannel(screen2),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
      "$BASE_URL/screen3" -> respond(
        content = ByteReadChannel(screen3),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
      else -> respond(
        content = ByteReadChannel(""),
        status = HttpStatusCode.NotFound,
      )
    }
}
