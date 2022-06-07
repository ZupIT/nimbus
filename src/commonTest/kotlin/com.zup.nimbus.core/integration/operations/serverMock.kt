package com.zup.nimbus.core.integration.operations

import com.zup.nimbus.core.integration.sendRequest.BASE_URL
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel

const val FIRST_PAGE = """{
  "_:component": "layout:container",
  "state": {
    "id": "count",
    "value": 1.0
  },
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "@{count]"
      }
    },
    {
      "_:component": "material:button",
      "id": "addToCount",
      "properties": {
        "text": "+1",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "count",
            "value": "@{sum(count, 1)}"
          }
        }]
      }
    }
  ]
}"""
